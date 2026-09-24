package com.yuanjingtech.aihao.content

/**
 * 一条审核发现。
 *
 * @property rule 命中的规则名。
 * @property target 命中的对象（爱好 id 或段位名）。
 * @property message 处理建议。
 */
data class AuditFinding(val rule: String, val target: String, val message: String)

/**
 * 内容自检，把规格 8.1 的审核标准里可自动化的部分固化下来，
 * 供 CI 与「提交新爱好提名」的审核后台复用。
 *
 * 人工审核仍需覆盖 8.1 的其余条款（政治、宗教、色情、暴力、性别与地域尊重）。
 */
object ContentAudit {

    /** 每段位主力爱好数量下限（规格 5.1：每层 6-7 个）。 */
    const val MIN_PER_TIER = 6

    /** 每段位主力爱好数量上限。 */
    const val MAX_PER_TIER = 7

    /** 段位名称长度上限，超过会在徽章上折行。 */
    const val MAX_RANK_NAME_LENGTH = 12

    /**
     * 贬低性词汇黑名单，仅覆盖自动化能可靠判定的部分，需随运营反馈增补。
     * 规格 8.1 允许自嘲（如「剁手党」「熬夜侠」），因此这里只收明确冒犯性表达。
     */
    private val BANNED_WORDS: List<String> = listOf(
        "废物", "垃圾", "废柴", "屌丝", "穷鬼", "剩女", "娘炮", "loser", "傻", "蠢",
    )

    /** 执行全部自检，返回发现列表；为空表示通过。 */
    fun findings(): List<AuditFinding> = buildList {
        addAll(checkTierSizes())
        addAll(checkUniqueIds())
        addAll(checkSuffixes())
        addAll(checkBannedWords())
        addAll(checkLimitedWindows())
        addAll(checkRankNameLength())
    }

    /** 是否通过全部自动检查。 */
    fun isClean(): Boolean = findings().isEmpty()

    private fun checkTierSizes(): List<AuditFinding> = buildList {
        Gender.values().forEach { gender ->
            Tier.values().forEach { tier ->
                val count = ChenmiSha.mainHobbies(gender, tier).size
                if (count !in MIN_PER_TIER..MAX_PER_TIER) {
                    add(
                        AuditFinding(
                            rule = "段位爱好数量",
                            target = "${gender.displayName}/${tier.displayName}",
                            message = "现有 $count 个，应在 $MIN_PER_TIER-$MAX_PER_TIER 之间",
                        ),
                    )
                }
            }
        }
    }

    private fun checkUniqueIds(): List<AuditFinding> = buildList {
        val all = buildList {
            Gender.values().forEach { addAll(ChenmiSha.allMainHobbies(it)) }
            ChenmiSha.limitedHobbies().forEach { add(it.hobby) }
        }
        all.groupBy { it.id }.filterValues { it.size > 1 }.forEach { (id, dup) ->
            add(AuditFinding("爱好 id 唯一", id, "重复 ${dup.size} 次"))
        }
    }

    private fun checkSuffixes(): List<AuditFinding> = buildList {
        val all = buildList {
            Gender.values().forEach { addAll(ChenmiSha.allMainHobbies(it)) }
            ChenmiSha.limitedHobbies().forEach { add(it.hobby) }
        }
        all.forEach { hobby ->
            val suffix = hobby.suffix ?: return@forEach
            val gender = hobby.gender ?: return@forEach
            if (!gender.supports(suffix)) {
                add(
                    AuditFinding(
                        rule = "身份后缀归属",
                        target = hobby.id,
                        message = "「${suffix.canonical}」不在${gender.displayName}后缀库中",
                    ),
                )
            }
        }
    }

    private fun checkBannedWords(): List<AuditFinding> = buildList {
        val texts = buildList {
            Gender.values().forEach { gender ->
                addAll(ChenmiSha.allMainHobbies(gender).map { it.name })
                addAll(ChenmiSha.tierCopies(gender).map { it.second })
            }
            ChenmiSha.limitedHobbies().forEach { add(it.hobby.name) }
            addAll(ChenmiSha.hiddenTags().map { it.description })
            addAll(ChenmiSha.easterEggs().map { it.reward })
        }
        texts.forEach { text ->
            BANNED_WORDS.filter { text.contains(it) }.forEach { word ->
                add(AuditFinding("贬低性用词", text, "命中黑名单词「$word」，请改写或复核"))
            }
        }
    }

    /** 只有节日限定（规格 7.1）带「持续时间」，地域与品牌限定由位置和联名活动触发。 */
    private fun checkLimitedWindows(): List<AuditFinding> = buildList {
        ChenmiSha.festivalHobbies().forEach { limited ->
            if (limited.windowLabel == null && limited.window == null) {
                add(
                    AuditFinding(
                        rule = "限定内容可见性",
                        target = limited.hobby.id,
                        message = "既没有 windowLabel 也没有 window，用户看不到生效时间",
                    ),
                )
            }
        }
    }

    private fun checkRankNameLength(): List<AuditFinding> = buildList {
        Gender.values().forEach { gender ->
            ChenmiSha.allMainHobbies(gender).forEach { hobby ->
                val name = ChenmiSha.rankName(gender, hobby.tier, hobby)
                if (name.text.length > MAX_RANK_NAME_LENGTH) {
                    add(
                        AuditFinding(
                            rule = "段位名称长度",
                            target = name.text,
                            message = "长度 ${name.text.length} 超过 $MAX_RANK_NAME_LENGTH，建议精简或设置 rankAlias",
                        ),
                    )
                }
            }
        }
    }
}
