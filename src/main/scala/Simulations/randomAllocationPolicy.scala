package Simulations

import HelperUtils.{CreateLogger, ObtainConfigReference}
import org.cloudbus.cloudsim.allocationpolicies.{VmAllocationPolicy, VmAllocationPolicySimple}
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.hosts.{Host, HostSimple}
import org.cloudbus.cloudsim.vms.Vm
import org.cloudsimplus.builders.tables.CloudletsTableBuilder

import scala.jdk.OptionConverters.*
import scala.jdk.javaapi.OptionConverters
import HelperUtils.utils.*

import collection.JavaConverters.*

class randomAllocationPolicy
object randomAllocationPolicy:
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
    vmAlloPolicy.setFindHostForVmFunction(createRandomAllocationPolicy);

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

  def createRandomAllocationPolicy(vmAlloPolicy:VmAllocationPolicy, vm:Vm): java.util.Optional[Host] =
    val hostList = vmAlloPolicy.getHostList().asScala;
    val r = scala.util.Random;
    for(hos <- hostList)
      val randomIndex = r.nextInt(hostList.size);
      val host = hostList.asJava.get(randomIndex);
      if(host.isSuitableForVm(vm))
        return OptionConverters.toJava(Option(host));
    return OptionConverters.toJava(Option.empty);




