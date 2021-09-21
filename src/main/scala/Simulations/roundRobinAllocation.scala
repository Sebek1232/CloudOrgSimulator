package Simulations

import HelperUtils.{CreateLogger, ObtainConfigReference}
import HelperUtils.utils.*
import org.cloudbus.cloudsim.allocationpolicies.*
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudsimplus.builders.tables.CloudletsTableBuilder
import collection.JavaConverters.*

class roundRobinAllocation
object roundRobinAllocation:
  val config = ObtainConfigReference("cloudSimulator") match {
    case Some(value) => value
    case None => throw new RuntimeException("Cannot obtain a reference to the config data.")
  }
  val logger = CreateLogger(classOf[BasicCloudSimPlusExample])

  def Start() =
    //create simulation object
    val cloudsim = new CloudSim();

    //create default allocation policy
    val vmAlloPolicy = new VmAllocationPolicyRoundRobin()

    //create datacenter
    val datacenter0 = createDatacenter(cloudsim, config.getInt("cloudSimulator.host.numOfHosts"),
      config.getInt("cloudSimulator.host.numOfPEs"), vmAlloPolicy);
    setCostValues(datacenter0);



    //default broker that host vms at the first datacenter it can find
    val broker0 = new DatacenterBrokerSimple(cloudsim);

    // create VM and cloudlet Lists
    val vmList = createVms(config.getInt("cloudSimulator.vm.numOfVMs"));
    val cloudletList = createCloudletList(config.getInt("cloudSimulator.cloudlet.numOfCloudlets"));

    // submits these lists to the broker
    broker0.submitVmList(vmList.asJava);
    broker0.submitCloudletList(cloudletList.asJava);

    //start simulation
    cloudsim.start();
    new CloudletsTableBuilder(broker0.getCloudletFinishedList()).build();
    printTotalCost(broker0,datacenter0);


