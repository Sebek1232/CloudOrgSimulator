import HelperUtils.{CreateLogger, ObtainConfigReference}
import Simulations.*
import com.typesafe.config.ConfigFactory
import org.cloudbus.cloudsim.allocationpolicies.*
import org.slf4j.LoggerFactory

object Simulation:
  val logger = CreateLogger(classOf[Simulation])

  @main def runSimulation =
    logger.info("Constructing a cloud model...")
      BasicFirstExample.Start(new VmAllocationPolicySimple);
      BasicFirstExample.Start(new VmAllocationPolicyFirstFit);
      BasicFirstExample.Start(new VmAllocationPolicyBestFit);
      BasicFirstExample.Start(new VmAllocationPolicyRoundRobin);
      spaceSharedScheduler.Start();
      timeSharedScheduler.Start();
    
    logger.info("Finished cloud simulation...")

class Simulation