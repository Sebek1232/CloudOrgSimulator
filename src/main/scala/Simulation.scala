import HelperUtils.{CreateLogger, ObtainConfigReference}
import Simulations.*
import com.typesafe.config.ConfigFactory
import org.cloudbus.cloudsim.allocationpolicies.*
import org.slf4j.LoggerFactory

object Simulation:
  val logger = CreateLogger(classOf[Simulation])

  @main def runSimulation =
    logger.info("Starting vmAllocationSimple Simulation")
    vmAllocationsSim.Start(new VmAllocationPolicySimple);
    
    logger.info("Starting vmAllocationFirstFit Simulation")
    vmAllocationsSim.Start(new VmAllocationPolicyFirstFit);
    
    logger.info("Starting vmAllocationBestFit Simulation")
    vmAllocationsSim.Start(new VmAllocationPolicyBestFit);
    
    logger.info("Starting vmAllocationRoundRobin Simulation")
    vmAllocationsSim.Start(new VmAllocationPolicyRoundRobin);
    
    logger.info("Starting spaceSharedScheduler Simulation")
    spaceSharedScheduler.Start();
    
    logger.info("Starting timeSharedScheduler Simulation")
    timeSharedScheduler.Start();
    
    logger.info("Starting broadcast Storm Simulation")
    broadcastStormSim.Start();
    

class Simulation