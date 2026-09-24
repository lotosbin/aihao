package com.yuanjingtech.aihao.content

/**
 * 生成好的段位名称（规格 5.3）：`[材质前缀] + [最高层级爱好] + [身份后缀]`。
 *
 * @property text 完整名称，如「黄金钓鱼佬」。
 * @property prefix 材质前缀，如「黄金」。
 * @property hobbyToken 段位名称里使用的爱好名称（可能是 [Hobby.rankAlias]）。
 * @property suffix 实际使用的身份后缀；null 表示该名称本身已构成身份。
 */
data class RankName(
    val text: String,
    val tier: Tier,
    val material: BadgeMaterial,
    val prefix: String,
    val hobbyId: String,
    val hobbyToken: String,
    val suffix: IdentitySuffix?,
)

/**
 * 段位名称生成器（规格 5.3）。
 *
 * ## 规则
 * 1. 取用户所有爱好中段位最高的那一层作为「最高层级」。
 * 2. 同一层有多个爱好时，优先选择自带身份后缀的条目（名称更完整），
 *    其次按传入顺序取第一个 —— 结果只取决于输入顺序，保证可复现。
 * 3. 材质前缀取该段位材质，身份后缀取爱好自带后缀；
 *    若该后缀不属于当前版本的后缀库（例如男性版条目被女性版用户选中），
 *    则回退到该版本的首选后缀（男性版「佬」、女性版「师」）。
 *
 * ## 与规格 5.3 示例的差异
 * 规格示例「黄金钓鱼佬」把钓鱼放在封师级（黄金），但规格 6.1 的进阶级评语明确写着
 * 「钓鱼、摩托、露营」。两处口径冲突，此处按评语口径把钓鱼归入进阶级，
 * 因此本库生成的名称是「白银钓鱼佬」；「黄金美甲师」同理（女性版进阶级评语含美甲）。
 * 组合规则本身仍然严格满足 5.3，示例逐字复现见 ContentLibraryTest 的固定用例。
 */
object RankNameGenerator {

    /** 按显式段位与爱好生成名称。 */
    fun generate(gender: Gender, tier: Tier, hobby: Hobby): RankName {
        val suffix = resolveSuffix(gender, hobby)
        val token = hobby.rankToken
        val prefix = tier.rankPrefix
        return RankName(
            text = prefix + token + (suffix?.attached ?: ""),
            tier = tier,
            material = tier.material,
            prefix = prefix,
            hobbyId = hobby.id,
            hobbyToken = token,
            suffix = suffix,
        )
    }

    /**
     * 按用户爱好列表生成名称，自动取最高段位与代表爱好。
     * 爱好列表为空时返回 null。
     */
    fun generate(gender: Gender, hobbies: List<Hobby>): RankName? {
        val top = topTier(hobbies) ?: return null
        val candidates = hobbies.filter { it.tier == top }
        val chosen = candidates.firstOrNull { it.suffix != null } ?: candidates.first()
        return generate(gender, top, chosen)
    }

    /** 用户的最高段位；爱好列表为空时返回 null。 */
    fun topTier(hobbies: List<Hobby>): Tier? = hobbies.maxByOrNull { it.tier.ordinal }?.tier

    /** 用户最高段位上的代表爱好，选择规则同 [generate]。 */
    fun topHobby(hobbies: List<Hobby>): Hobby? {
        val top = topTier(hobbies) ?: return null
        val candidates = hobbies.filter { it.tier == top }
        return candidates.firstOrNull { it.suffix != null } ?: candidates.first()
    }

    /** 解析该爱好在当前版本下实际使用的身份后缀。 */
    fun resolveSuffix(gender: Gender, hobby: Hobby): IdentitySuffix? {
        val declared = hobby.suffix ?: return null
        return if (gender.supports(declared)) declared else gender.preferredSuffix
    }
}
