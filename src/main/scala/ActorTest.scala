import akka.actor._

object ActorTest extends App {

  val system = ActorSystem("PingPongSystem")
  val actorPong = system.actorOf(Props[ActorPong], "ActorPong")
  val actorPing = system.actorOf(Props(new ActorPing(actorPong)), "ActorPing")
  actorPing ! "start"
  case object Pong
  case object Ping
  system.terminate()
  class ActorPing(actorPong: ActorRef) extends Actor {
    var counter = 5

    def receive = {
      case "start" =>
        println("Актор ПИНГ: Отправляю сообщение ПИНГ актору ПОНГ")
        actorPong ! Ping
      case Pong =>
        counter -= 1
        if (counter > 0) {
          println("Актор ПИНГ: получил сообщение ПОНГ от актора ПОНГ, отправляю ПИНГ")
          actorPong ! Ping
        } else {
          println("Актор ПИНГ: Лимит сообщений исчерпан, завершение.")
          context.stop(self)
        }
    }

    override def preStart(): Unit = {
      println(s"Актор ПИНГ: количество обрабатываемых сообщение равно $counter")
    }
  }

  class ActorPong extends Actor {

    def receive = {
      case Ping =>
        println("Актор ПОНГ: получил сообщение ПИНГ от актора ПИНГ, отправляю ПОНГ")
        sender() ! Pong
      case _ =>
        println("Актор ПОНГ: получил сообщение ПИНГ от актора ПИНГ, отправляю ПОНГ")
        sender() ! Pong
    }

  }
}
