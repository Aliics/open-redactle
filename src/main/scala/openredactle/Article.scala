package openredactle

import com.raquo.laminar.api.L.{*, given}

object Article:
  def renderElement: Element =
    div(
      height := "100%",
      overflow := "scroll",

      h1("Lorem Ipsum"),
      """
        |
        |
        |Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean auctor dictum nulla, nec condimentum odio efficitur ut. Duis vestibulum id tellus in sollicitudin. Nulla sed velit euismod, ultrices libero vitae, rhoncus nulla. Nulla dignissim pellentesque quam, auctor eleifend libero fringilla nec. Nullam dapibus dui a nisi vulputate dapibus. Aenean vulputate facilisis nibh, a vehicula dolor suscipit quis. Interdum et malesuada fames ac ante ipsum primis in faucibus. In rutrum leo ipsum, ut vehicula risus interdum vitae. Mauris viverra laoreet rhoncus.
        |
        |Donec fringilla metus augue, et consequat felis tempor at. Cras quam ante, ornare et commodo eget, euismod in odio. Donec maximus, ipsum quis tincidunt venenatis, nisi nisl viverra purus, sed feugiat nisl nibh et enim. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Ut eget libero vel purus placerat feugiat. In maximus nisl dui, placerat gravida mi volutpat ut. Morbi faucibus lectus id magna aliquet scelerisque. Fusce justo lorem, ultrices sit amet enim eu, ullamcorper lacinia ante. Suspendisse libero purus, pretium elementum laoreet sit amet, vestibulum in tellus. Nullam congue nec nibh in convallis. Nam interdum, elit et aliquet suscipit, magna tortor vulputate justo, ut condimentum nibh mauris sed felis. Phasellus ut molestie diam. Nunc sit amet arcu scelerisque, lacinia nunc ut, iaculis lectus.
        |
        |Etiam venenatis sollicitudin eros. Sed at volutpat sem. Cras nec lectus ornare, porttitor ante iaculis, vestibulum odio. Integer sed leo erat. Suspendisse convallis cursus massa at rhoncus. Vivamus vel luctus leo. Mauris vulputate, massa nec pellentesque dapibus, nisi turpis feugiat urna, quis mollis neque massa eget metus. Nunc placerat lacinia velit. Nulla facilisi. Proin volutpat iaculis ullamcorper. In hac habitasse platea dictumst. Pellentesque a condimentum nisi. Sed pulvinar nulla sit amet neque cursus, eu efficitur leo ornare. Sed tellus eros, viverra ac fringilla ac, dignissim eu libero. Nunc viverra blandit felis in scelerisque. Phasellus gravida viverra ipsum, vel interdum velit ultricies quis.
        |
        |Nullam tincidunt lectus vel libero rhoncus, non vehicula lorem gravida. Phasellus ullamcorper feugiat metus gravida luctus. Etiam suscipit ac arcu eu faucibus. Morbi semper commodo nisi at tristique. Praesent id arcu vitae metus tincidunt interdum vel at augue. In volutpat lectus sit amet est consectetur facilisis. Nam aliquam purus at tortor tincidunt, id pellentesque elit ultrices. Fusce vestibulum fringilla tellus, eu ullamcorper augue tincidunt id. Ut lacinia luctus rhoncus. Vestibulum erat libero, accumsan eu euismod vitae, condimentum sagittis nisi. Nulla leo ex, sodales ac quam vel, scelerisque pellentesque nisl. Etiam ac luctus tortor.
        |
        |Vestibulum consequat, eros ultrices placerat laoreet, felis neque sagittis velit, vel imperdiet nisl purus a quam. Nam consectetur, lorem at euismod lobortis, leo orci posuere metus, vitae convallis orci ex vitae ante. Aliquam accumsan sollicitudin nulla, vel rutrum eros dictum nec. Curabitur eget aliquet tellus. Quisque tempus vehicula nulla, non tincidunt mi ullamcorper eget. Etiam fringilla molestie lacus, sed ultricies dolor sollicitudin vel. Donec at erat id odio ultrices feugiat id gravida ligula. Aenean imperdiet justo ipsum, a laoreet libero sodales vitae. Duis condimentum justo mi, id laoreet arcu semper non. Ut augue sem, sagittis nec arcu ut, mattis feugiat dui. Vivamus sagittis sed neque id porttitor. Suspendisse imperdiet, diam eu laoreet tincidunt, augue nisl dictum libero, ac egestas lorem elit at purus. Praesent placerat lacus est, quis tempus magna ultrices vitae.
        |""".stripMargin)
