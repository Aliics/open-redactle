package openredactle.server.data

import openredactle.shared.data.{ArticleData, Word}
import openredactle.shared.data.ArticleData.{Header, Paragraph, Title}
import openredactle.shared.{data, roughEquals, wordsFromString}

val freeWords = List("or", "as", "a", "of", "and", "in", "the", "by", "if", "to", "be", "s")

val dummyRabbitArticleData: List[ArticleData] =
  List(
    Title(wordsFromString("Domestic rabbit")),
    Paragraph(wordsFromString("The domestic or domesticated rabbit—more commonly known as a pet rabbit, bunny, bun, or bunny rabbit—is the domesticated form of the European rabbit, a member of the lagomorph order. A male rabbit is known as a buck, a female is a doe, and a young rabbit is a kit, or kitten.")),
    Paragraph(wordsFromString("Rabbits were first used for their food and fur by the Romans, and have been kept as pets in Western nations since the 19th century. Rabbits can be housed in exercise pens, but free roaming without any boundaries in a rabbit-proofed space has become popularized on social media in recent years. Beginning in the 1980s, the idea of the domestic rabbit as a house companion, a so-called house rabbit similar to a house cat, was promoted. Rabbits can be litter box-trained and taught to come when called, but they require exercise and can damage a house that has not been \"rabbit proofed\" based on their innate need to chew. Accidental interactions between pet rabbits and wild rabbits, while seemingly harmless, are usually strongly discouraged due to the species' different temperaments as well as wild rabbits potentially carrying diseases.")),
    Paragraph(wordsFromString("Unwanted pet rabbits end up in animal shelters, especially after the Easter season (see Easter Bunny). In 2017, they were the United States' third most abandoned pet. Some of them go on to be adopted and become family pets in various forms. Because their wild counterparts have become invasive in Australia, pet rabbits are banned in the state of Queensland. Pet rabbits, being a domesticated breed that lack survival instincts, do not fare well in the wild if they are abandoned or escape from captivity.")),

    Header(wordsFromString("History")),
    Paragraph(wordsFromString("Phoenician sailors visiting the coast of Spain c. 12th century BC, mistaking the European rabbit for a species from their homeland (the rock hyrax Procavia capensis), gave it the name i-shepan-ham (land or island of hyraxes).")),
    Paragraph(wordsFromString("The captivity of rabbits as a food source is recorded as early as the 1st century BC, when the Roman writer Pliny the Elder described the use of rabbit hutches, along with enclosures called leporaria. A controversial theory is that a corruption of the rabbit's name used by the Romans became the Latin name for the peninsula, Hispania. In Rome, rabbits were raised in large walled colonies with walls extended underground. According to Pliny, the consumption of unborn and newborn rabbits, called laurices, was considered a delicacy.")),
    Paragraph(wordsFromString("Evidence for the domestic rabbit is rather late. In the Middle Ages, wild rabbits were often kept for the hunt. Monks in southern France were crossbreeding rabbits at least by the 12th century AD. Domestication was probably a slow process that took place from the Roman period (or earlier) until the 1500s.")),
    Paragraph(wordsFromString("In the 19th century, as animal fancy in general began to emerge, rabbit fanciers began to sponsor rabbit exhibitions and fairs in Western Europe and the United States. Breeds of various domesticated animals were created and modified for the added purpose of exhibition, a departure from the breeds that had been created solely for food, fur, or wool. The rabbit's emergence as a household pet began during the Victorian era.")),
    Paragraph(wordsFromString("The keeping of the rabbit as a pet commencing from the 1800s coincides with the first observable skeletal differences between the wild and domestic populations, even though captive rabbits had been exploited for over 2,000 years.[1] Domestic rabbits have been popular in the United States since the late 19th century. What became known as the \"Belgian Hare Boom\" began with the importation of the first Belgian Hares from England in 1888 and, soon after, the founding of the American Belgian Hare Association, the first rabbit club in America. From 1898 to 1901, many thousands of Belgian Hares were imported to America.[13] Today, the Belgian Hare is one of the rarest breeds, with only 132 specimens found in the United States in a 2015 census.")),
    Paragraph(wordsFromString("The American Rabbit Breeders Association (ARBA) was founded in 1910 and is the national authority on rabbit raising and rabbit breeds having a uniform Standard of Perfection, registration and judging system. The domestic rabbit continues to be popular as a show animal and pet. Many thousand rabbit shows occur each year and are sanctioned in Canada and the United States by the ARBA. Today, the domesticated rabbit is the third most popular mammalian pet in Britain after dogs and cats. ")),

    Header(wordsFromString("Experimentation")),
    Paragraph(wordsFromString("Rabbits have been, and continue to be, used in laboratory work such as the production of antibodies for vaccines and research of human male reproductive system toxicology. The Environmental Health Perspective, published by the National Institute of Health, states, \"The rabbit [is] an extremely valuable model for studying the effects of chemicals or other stimuli on the male reproductive system.\" According to the Humane Society of the United States, rabbits are also used extensively in the study of bronchial asthma, stroke prevention treatments, cystic fibrosis, diabetes, and cancer. Animal rights activists have opposed animal experimentation for non-medical purposes, such as the testing of cosmetic and cleaning products, which has resulted in decreased use of rabbits in these areas.")),

    Header(wordsFromString("Terminology")),
    Paragraph(wordsFromString("Male rabbits are called bucks; females are called does. An older term for an adult rabbit is coney, while rabbit once referred only to the young animals.[16] Another term for a young rabbit is bunny, though this term is often applied informally (especially by children and rabbit enthusiasts) to rabbits generally, especially domestic ones. More recently, the term kit or kitten has been used to refer to a young rabbit. A young hare is called a leveret; this term is sometimes informally applied to a young rabbit as well. A group of rabbits is known as a \"colony\" or a \"nest\".[17] House rabbit enthusiasts may call their group of house rabbits a \"fluffle\".")),
  )

def getMatchingGuessData(fullArticleData: Seq[ArticleData])(guess: String): (Map[Word.Known, Seq[(Int, Seq[Int])]], Int) =
  val matches = fullArticleData.map: articleData =>
    articleData.words.zipWithIndex.collect:
      case (known: Word.Known, idx) if roughEquals(known.str)(guess) => known -> idx
  .map(_.groupMap(_._1)(_._2))
  .zipWithIndex
  .filter(_._1.nonEmpty)
  .flatMap: (strs, i) =>
    strs.map((s, is) => s -> (i, is))
  .groupMap(_._1)(_._2)

  val matchedCount = matches.map(_._2.map(_._2.length).sum).sum

  matches -> matchedCount
  