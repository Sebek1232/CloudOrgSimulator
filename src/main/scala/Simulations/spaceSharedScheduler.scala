package Simulations

import HelperUtils.{CreateLogger, ObtainConfigReference}
import HelperUtils.utils.*
import org.cloudbus.cloudsim.allocationpolicies.{VmAllocationPolicyRoundRobin, VmAllocationPolicySimple}
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudsimplus.builders.tables.CloudletsTableBuilder

import collection.JavaConverters.*

class spaceSharedScheduler 
object spaceSharedScheduler:
  val config = ObtainConfigReference("cloudSimulator") match {
    case Some(value) => value
    case None => throw new RuntimeException("Cannot obtain a reference to the config data.")
  }
  val logger = CreateLogger(classOf[BasicCloudSimPlusExample])

  def Start() =
    //create simulation object
    val cloudsim = new CloudSim();

    //create default allocation policy
    val vmAlloPolicy = new VmAllocationPolicySimple();

    //create datacenter
    val datacenter0 = createDatacenterWithTimeShared(cloudsim, config.getInt("cloudSimulator.host.numOfHosts"),
      config.getInt("cloudSimulator.host.numOfPEs"), vmAlloPolicy);
    setCostValues(datacenter0);
    logger.info("SpaceSharedScheduler: Created one datacenter");



    //default broker that host vms at the first datacenter it can find
    val broker0 = new DatacenterBrokerSimple(cloudsim);
    logger.info("SpaceSharedScheduler: Created one broker");

    // create VM and cloudlet Lists
    val vmList = createVmsWithSpaceShared(config.getInt("cloudSimulator.vm.numOfVMs"));
    logger.info("SpaceSharedScheduler: Created list of vms");
    
    val cloudletList = createCloudletList(config.getInt("cloudSimulator.cloudlet.numOfCloudlets2"),
      config.getDouble("cloudSimulator.utilizationRatio2"));
    logger.info("SpaceSharedScheduler: Created list of cloudlets");
    

    // submits these lists to the broker
    broker0.submitVmList(vmList.asJava);
    logger.info("SpaceSharedScheduler: Submitted vm list to broker");
    
    broker0.submitCloudletList(cloudletList.asJava);
    logger.info("SpaceSharedScheduler: Submitted cloudlet list to broker");

    //start simulation
    logger.info("SpaceSharedScheduler: Similation about to start");
    cloudsim.start();
    new CloudletsTableBuilder(broker0.getCloudletFinishedList()).build();
    printTotalCost(broker0,datacenter0);
    printCpuUtilOfVms(broker0);

  
