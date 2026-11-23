package com.example.birdnursery.data.model

/**
 * Генетическая модель амадина Гульда по калькулятору gouldianfinches.eu (v1.1).
 * Подробный разбор: docs/gouldian-genetics.md.
 *
 * Коды генотипов (как в формах сайта):
 *  - голова самца: [R|b][R|b][o|i][o|i], самки: [R|b]Y[o|i][o|i]
 *  - грудь: [P|l|w][P|l|w]
 *  - тело самца: [G|I][G|I][o|m][o|m], самки: [G|I]Y[o|m][o|m]
 */
object GouldianGenetics {

    enum class Sex { MALE, FEMALE }

    fun sexOf(sexLabel: String): Sex = if (sexLabel == "Самка") Sex.FEMALE else Sex.MALE

    data class GenotypeOption(val code: String, val label: String)

    val maleHeadOptions = listOf(
        GenotypeOption("RRoo", "красноголовый"),
        GenotypeOption("Rboo", "красноголовый / чёрноголовый"),
        GenotypeOption("RRio", "красноголовый / жёлтоголовый"),
        GenotypeOption("Rbio", "красноголовый / чёрноголовый / жёлтоголовый"),
        GenotypeOption("bboo", "чёрноголовый"),
        GenotypeOption("bbii", "чёрноголовый, ген. жёлтоголовый (ЖКК)"),
        GenotypeOption("bbio", "чёрноголовый / жёлтоголовый"),
        GenotypeOption("RRii", "жёлтоголовый"),
        GenotypeOption("Rbii", "жёлтоголовый / чёрноголовый")
    )

    val femaleHeadOptions = listOf(
        GenotypeOption("RYoo", "красноголовая"),
        GenotypeOption("RYio", "красноголовая / жёлтоголовая"),
        GenotypeOption("RYii", "жёлтоголовая"),
        GenotypeOption("bYoo", "чёрноголовая"),
        GenotypeOption("bYii", "чёрноголовая, ген. жёлтоголовая (ЖКК)"),
        GenotypeOption("bYio", "чёрноголовая / жёлтоголовая")
    )

    val breastOptions = listOf(
        GenotypeOption("PP", "фиолетовая"),
        GenotypeOption("Pl", "фиолетовая / розовая"),
        GenotypeOption("Pw", "фиолетовая / белая"),
        GenotypeOption("ll", "розовая"),
        GenotypeOption("lw", "розовая / белая"),
        GenotypeOption("ww", "белая")
    )

    val maleBodyOptions = listOf(
        GenotypeOption("GGoo", "зелёный"),
        GenotypeOption("GIoo", "жёлтый ОФ (зелёный пастельный)"),
        GenotypeOption("IIoo", "жёлтый ДФ (золотой)"),
        GenotypeOption("GGmm", "синий"),
        GenotypeOption("GImm", "синий пастельный ОФ"),
        GenotypeOption("IImm", "серебристый ДФ"),
        GenotypeOption("GGmo", "зелёный / щеп. синий"),
        GenotypeOption("GImo", "жёлтый ОФ / щеп. синий"),
        GenotypeOption("IImo", "жёлтый ДФ (золотой) / щеп. синий")
    )

    val femaleBodyOptions = listOf(
        GenotypeOption("GYoo", "зелёная"),
        GenotypeOption("IYoo", "жёлтая"),
        GenotypeOption("GYmm", "синяя"),
        GenotypeOption("IYmm", "серебристая"),
        GenotypeOption("GYmo", "зелёная / щеп. синяя"),
        GenotypeOption("IYmo", "жёлтая / щеп. синяя")
    )

    fun headOptions(sexLabel: String) =
        if (sexOf(sexLabel) == Sex.FEMALE) femaleHeadOptions else maleHeadOptions

    fun bodyOptions(sexLabel: String) =
        if (sexOf(sexLabel) == Sex.FEMALE) femaleBodyOptions else maleBodyOptions

    // ------------------------------------------------------------------
    // Фенотипы
    // ------------------------------------------------------------------

    enum class HeadPhenotype { RED, BLACK, YELLOW, BLACK_GEN_YELLOW }
    enum class BreastPhenotype { PURPLE, LILAC, WHITE }
    enum class BodyPhenotype { GREEN, YELLOW_SF, YELLOW_DF, BLUE, PASTEL_BLUE, SILVER_DF }

    private data class HeadAlleles(val a1: Char, val a2: Char, val y1: Char, val y2: Char)
    private data class BodyAlleles(val g1: Char, val g2: Char, val b1: Char, val b2: Char)

    private fun parseHead(code: String) = HeadAlleles(
        a1 = code.getOrElse(0) { 'R' },
        a2 = code.getOrElse(1) { 'R' },
        y1 = code.getOrElse(2) { 'o' },
        y2 = code.getOrElse(3) { 'o' }
    )

    private fun parseBody(code: String) = BodyAlleles(
        g1 = code.getOrElse(0) { 'G' },
        g2 = code.getOrElse(1) { 'G' },
        b1 = code.getOrElse(2) { 'o' },
        b2 = code.getOrElse(3) { 'o' }
    )

    private fun headPhenotype(h: HeadAlleles): HeadPhenotype {
        val hasRed = h.a1 == 'R' || h.a2 == 'R'
        val yellowHomo = h.y1 == 'i' && h.y2 == 'i'
        return when {
            yellowHomo && hasRed -> HeadPhenotype.YELLOW
            yellowHomo -> HeadPhenotype.BLACK_GEN_YELLOW
            hasRed -> HeadPhenotype.RED
            else -> HeadPhenotype.BLACK
        }
    }

    fun headPhenotype(headCode: String): HeadPhenotype = headPhenotype(parseHead(headCode))

    fun breastPhenotype(breastCode: String): BreastPhenotype = when {
        breastCode.contains('P') -> BreastPhenotype.PURPLE
        breastCode.contains('l') -> BreastPhenotype.LILAC
        else -> BreastPhenotype.WHITE
    }

    private fun bodyPhenotype(b: BodyAlleles): BodyPhenotype {
        val iCount = listOf(b.g1, b.g2).count { it == 'I' }
        val blue = b.b1 == 'm' && b.b2 == 'm'
        return when {
            blue && iCount == 2 -> BodyPhenotype.SILVER_DF
            blue && iCount == 1 -> BodyPhenotype.PASTEL_BLUE
            blue -> BodyPhenotype.BLUE
            iCount == 2 -> BodyPhenotype.YELLOW_DF
            iCount == 1 -> BodyPhenotype.YELLOW_SF
            else -> BodyPhenotype.GREEN
        }
    }

    fun bodyPhenotype(bodyCode: String): BodyPhenotype = bodyPhenotype(parseBody(bodyCode))

    // ------------------------------------------------------------------
    // Подписи фенотипов
    // ------------------------------------------------------------------

    fun headLabel(h: HeadPhenotype, sex: Sex): String = when (h) {
        HeadPhenotype.RED -> if (sex == Sex.FEMALE) "красноголовая" else "красноголовый"
        HeadPhenotype.BLACK -> if (sex == Sex.FEMALE) "чёрноголовая" else "чёрноголовый"
        HeadPhenotype.YELLOW -> if (sex == Sex.FEMALE) "жёлтоголовая" else "жёлтоголовый"
        HeadPhenotype.BLACK_GEN_YELLOW ->
            if (sex == Sex.FEMALE) "чёрноголовая (ген. жёлтая)" else "чёрноголовый (ген. жёлтый)"
    }

    fun breastLabel(b: BreastPhenotype): String = when (b) {
        BreastPhenotype.PURPLE -> "фиолетовая"
        BreastPhenotype.LILAC -> "розовая"
        BreastPhenotype.WHITE -> "белая"
    }

    fun bodyLabel(b: BodyPhenotype, sex: Sex): String = when (b) {
        BodyPhenotype.GREEN -> if (sex == Sex.FEMALE) "зелёная" else "зелёный"
        BodyPhenotype.YELLOW_SF -> if (sex == Sex.FEMALE) "жёлтая" else "жёлтый ОФ"
        BodyPhenotype.YELLOW_DF -> if (sex == Sex.FEMALE) "жёлтая" else "жёлтый ДФ (золотой)"
        BodyPhenotype.BLUE -> if (sex == Sex.FEMALE) "синяя" else "синий"
        BodyPhenotype.PASTEL_BLUE -> if (sex == Sex.FEMALE) "серебристая" else "синий пастельный ОФ"
        BodyPhenotype.SILVER_DF -> if (sex == Sex.FEMALE) "серебристая" else "серебристый ДФ"
    }

    fun phenotypeLabel(headCode: String, breastCode: String, bodyCode: String, sex: Sex): String =
        "${headLabel(headPhenotype(headCode), sex)}, " +
            "${breastLabel(breastPhenotype(breastCode))}, " +
            bodyLabel(bodyPhenotype(bodyCode), sex)

    fun phenotypeLabel(bird: Bird): String =
        phenotypeLabel(bird.headGenotype, bird.breastGenotype, bird.bodyGenotype, sexOf(bird.sex))

    // ------------------------------------------------------------------
    // Имя файла картинки (assets/genetics/images)
    // ------------------------------------------------------------------

    fun imageFile(
        head: HeadPhenotype,
        breast: BreastPhenotype,
        body: BodyPhenotype,
        sex: Sex,
        available: Set<String>
    ): String? {
        val headCode = when (sex) {
            Sex.MALE -> when (head) {
                HeadPhenotype.RED -> "RRoo"
                HeadPhenotype.BLACK -> "bboo"
                HeadPhenotype.YELLOW -> "RRii"
                HeadPhenotype.BLACK_GEN_YELLOW -> "bbii"
            }
            Sex.FEMALE -> when (head) {
                HeadPhenotype.RED -> "RYoo"
                HeadPhenotype.BLACK -> "bYoo"
                HeadPhenotype.YELLOW -> "RYii"
                HeadPhenotype.BLACK_GEN_YELLOW -> "bYii"
            }
        }
        val breastCode = when (breast) {
            BreastPhenotype.PURPLE -> "PP"
            BreastPhenotype.LILAC -> "ll"
            BreastPhenotype.WHITE -> "ww"
        }

        fun bodyImageCode(b: BodyPhenotype): String = when (sex) {
            Sex.MALE -> when (b) {
                BodyPhenotype.GREEN -> "GGoo"
                BodyPhenotype.YELLOW_SF ->
                    if (breast == BreastPhenotype.PURPLE) "GIoo-pastel" else "GIoo-SF"
                BodyPhenotype.YELLOW_DF -> "IIoo-DF"
                BodyPhenotype.BLUE -> "GGmm"
                BodyPhenotype.PASTEL_BLUE -> "GImm"
                BodyPhenotype.SILVER_DF -> "IImm-DF"
            }
            Sex.FEMALE -> when (b) {
                BodyPhenotype.GREEN -> "GYoo"
                BodyPhenotype.YELLOW_SF, BodyPhenotype.YELLOW_DF -> "IYoo"
                BodyPhenotype.BLUE -> "GYmm"
                BodyPhenotype.PASTEL_BLUE, BodyPhenotype.SILVER_DF -> "IYmm"
            }
        }

        val direct = "$headCode$breastCode${bodyImageCode(body)}.gif"
        if (direct in available) return direct

        // Жёлтоголовые (ii) с синим телом на сайте не отрисованы — берём версию без синего
        val withoutBlue = when (body) {
            BodyPhenotype.BLUE -> BodyPhenotype.GREEN
            BodyPhenotype.PASTEL_BLUE -> BodyPhenotype.YELLOW_SF
            BodyPhenotype.SILVER_DF -> BodyPhenotype.YELLOW_DF
            else -> body
        }
        if (withoutBlue != body) {
            val fallback = "$headCode$breastCode${bodyImageCode(withoutBlue)}.gif"
            if (fallback in available) return fallback
        }

        val green = "$headCode$breastCode${bodyImageCode(BodyPhenotype.GREEN)}.gif"
        return if (green in available) green else null
    }

    fun imageFile(
        headCode: String,
        breastCode: String,
        bodyCode: String,
        sex: Sex,
        available: Set<String>
    ): String? = imageFile(
        headPhenotype(headCode), breastPhenotype(breastCode), bodyPhenotype(bodyCode), sex, available
    )

    // ------------------------------------------------------------------
    // Конвертация кодов при смене пола (через фенотип)
    // ------------------------------------------------------------------

    fun convertHeadForSex(code: String, toFemale: Boolean): String {
        val h = parseHead(code)
        val red = h.a1 == 'R' || h.a2 == 'R'
        val yellow = "${h.y1}${h.y2}"
        return if (toFemale) {
            (if (red) "RY" else "bY") + yellow
        } else {
            (if (red) "RR" else "bb") + yellow
        }
    }

    fun convertBodyForSex(code: String, toFemale: Boolean): String {
        val b = parseBody(code)
        val yellow = b.g1 == 'I' || b.g2 == 'I'
        val blue = "${b.b1}${b.b2}"
        return if (toFemale) {
            (if (yellow) "IY" else "GY") + blue
        } else {
            (if (yellow) "II" else "GG") + blue
        }
    }

    // ------------------------------------------------------------------
    // Прогноз потомства (менделевское скрещивание, как на сайте)
    // ------------------------------------------------------------------

    data class ChickPrediction(
        val sex: Sex,
        /** доля от всего выводка, 0..1 (самцы и самки в сумме дают 1.0) */
        val probability: Double,
        val head: HeadPhenotype,
        val breast: BreastPhenotype,
        val body: BodyPhenotype
    ) {
        val label: String
            get() = "${headLabel(head, sex)}, ${breastLabel(breast)}, ${bodyLabel(body, sex)}"
    }

    fun predictOffspring(
        fatherHead: String, fatherBreast: String, fatherBody: String,
        motherHead: String, motherBreast: String, motherBody: String
    ): List<ChickPrediction> {
        val fh = parseHead(fatherHead)
        val mh = parseHead(motherHead)
        val fbo = parseBody(fatherBody)
        val mbo = parseBody(motherBody)

        fun gametes(a1: Char, a2: Char): List<Pair<Char, Double>> =
            if (a1 == a2) listOf(a1 to 1.0) else listOf(a1 to 0.5, a2 to 0.5)

        data class Key(
            val sex: Sex,
            val head: HeadPhenotype,
            val breast: BreastPhenotype,
            val body: BodyPhenotype
        )

        val acc = LinkedHashMap<Key, Double>()

        for (sex in Sex.entries) {
            for ((headZ, headZp) in gametes(fh.a1, fh.a2)) {
                // сын получает Z матери, дочь — W (вторая позиция = Y)
                val headA2 = if (sex == Sex.MALE) mh.a1 else 'Y'
                for ((fy, fyp) in gametes(fh.y1, fh.y2))
                    for ((my, myp) in gametes(mh.y1, mh.y2))
                        for ((fb, fbp) in gametes(fatherBreast[0], fatherBreast[1]))
                            for ((mb, mbp) in gametes(motherBreast[0], motherBreast[1]))
                                for ((bodyZ, bodyZp) in gametes(fbo.g1, fbo.g2))
                                    for ((fbl, fblp) in gametes(fbo.b1, fbo.b2))
                                        for ((mbl, mblp) in gametes(mbo.b1, mbo.b2)) {
                                            val bodyG2 = if (sex == Sex.MALE) mbo.g1 else 'Y'
                                            val probability = 0.5 * headZp * fyp * myp * fbp *
                                                mbp * bodyZp * fblp * mblp
                                            val key = Key(
                                                sex = sex,
                                                head = headPhenotype(HeadAlleles(headZ, headA2, fy, my)),
                                                breast = breastPhenotype("$fb$mb"),
                                                body = bodyPhenotype(BodyAlleles(bodyZ, bodyG2, fbl, mbl))
                                            )
                                            acc.merge(key, probability, Double::plus)
                                        }
            }
        }

        return acc.map { (key, p) ->
            ChickPrediction(key.sex, p, key.head, key.breast, key.body)
        }.sortedWith(compareBy({ it.sex }, { -it.probability }))
    }

    fun formatPercent(probability: Double): String {
        val pct = probability * 100.0
        val rounded = Math.round(pct * 10.0) / 10.0
        return if (rounded == Math.floor(rounded)) "${rounded.toInt()}%" else "$rounded%"
    }
}
