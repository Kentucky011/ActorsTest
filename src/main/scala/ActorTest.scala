import akka.actor._

object ActorTest extends App {

  private val system = ActorSystem("PingPongSystem")
  private val actorPong = system.actorOf(Props[ActorPong], "ActorPong")
  private val actorPing = system.actorOf(Props(new ActorPing(actorPong)), "ActorPing")
  actorPing ! "start"
  private case object Pong
  private case object Ping
  system.terminate()
  private class ActorPing(actorPong: ActorRef) extends Actor {
    private val counter = 5

    def receive: Receive = onMessage(counter)

    override def preStart(): Unit = {
      println(s"Актор ПИНГ: количество обрабатываемых сообщение равно $counter")
    }

    private def onMessage(counter: Int): Receive = {
      case "start" =>
        println("Актор ПИНГ: Отправляю сообщение ПИНГ актору ПОНГ")
        actorPong ! Ping
      case Pong =>
        context.become(onMessage(counter - 1))
        if (counter > 0) {
          println("Актор ПИНГ: получил сообщение ПОНГ от актора ПОНГ, отправляю ПИНГ")
          actorPong ! Ping
        } else {
          println("Актор ПИНГ: Лимит сообщений исчерпан, завершение.")
          context.stop(self)
        }
    }
  }

  private class ActorPong extends Actor {

    def receive: Receive = {
      case Ping =>
        println("Актор ПОНГ: получил сообщение ПИНГ от актора ПИНГ, отправляю ПОНГ")
        sender() ! Pong
      case _ =>
        println("Актор ПОНГ: получил сообщение ПИНГ от актора ПИНГ, отправляю ПОНГ")
        sender() ! Pong
    }

  }
}
