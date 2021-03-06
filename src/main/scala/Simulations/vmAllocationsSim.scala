package Simulations

import HelperUtils.{CreateLogger, ObtainConfigReference}
import HelperUtils.utils.{config, *}

import org.cloudbus.cloudsim.allocationpolicies.*
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.{Datacenter, DatacenterSimple}
import org.cloudbus.cloudsim.hosts.{Host, HostSimple}
import org.cloudbus.cloudsim.resources.{Pe, PeSimple}
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic
import org.cloudbus.cloudsim.vms.{Vm, VmSimple}
import org.cloudsimplus.builders.tables.CloudletsTableBuilder

import java.util.Optional
import collection.JavaConverters.*



class vmAllocationsSim

object vmAllocationsSim:
  val config = ObtainConfigReference("cloudSimulator") match {
    case Some(value) => value
    case None => throw new RuntimeException("Cannot obtain a reference to the config data.")
  }
  val logger = CreateLogger(classOf[BasicCloudSimPlusExample])

  def Start(vmAlloPolicy: VmAllocationPolicy) =
    //create simulation object
    val cloudsim = new CloudSim();

    //create three diffrent PE objects
    val hostPes0 = List.fill(config.getInt("cloudSimulator.host0.numOfPEs"))(
      new PeSimple(config.getLong("cloudSimulator.host0.mipsCapacity")));
    val hostPes1 = List.fill(config.getInt("cloudSimulator.host1.numOfPEs"))(
      new PeSimple(config.getLong("cloudSimulator.host1.mipsCapacity")));
    val hostPes2 = List.fill(config.getInt("cloudSimulator.host2.numOfPEs"))(
      new PeSimple(config.getLong("cloudSimulator.host2.mipsCapacity")));

    //create three diffrent hosts
    val Host0 = new HostSimple(config.getLong("cloudSimulator.host0.RAMInMBs"),
      config.getLong("cloudSimulator.host0.StorageInMBs"),
      config.getLong("cloudSimulator.host0.BandwidthInMBps"),
      hostPes0.asJava);
    val Host1 = new HostSimple(config.getLong("cloudSimulator.host1.RAMInMBs"),
      config.getLong("cloudSimulator.host1.StorageInMBs"),
      config.getLong("cloudSimulator.host1.BandwidthInMBps"),
      hostPes1.asJava);
    val Host2 = new HostSimple(config.getLong("cloudSimulator.host2.RAMInMBs"),
      config.getLong("cloudSimulator.host2.StorageInMBs"),
      config.getLong("cloudSimulator.host2.BandwidthInMBps"),
      hostPes2.asJava);
    val hostList = List(Host0,Host1, Host2);
    logger.info("vmAllocationSim: created list of three hosts");

    //creates one datacenter using the previous hosts. Allocation policy is pass into start
    val datacenter0 = new DatacenterSimple(cloudsim, hostList.asJava, vmAlloPolicy);
    setCostValues(datacenter0);
    logger.info("vmAllocationSim: Created one datacenter");

    //default broker that host vms at the first datacenter it can find
    val broker0 = new DatacenterBrokerSimple(cloudsim);
    logger.info("vmAllocationSim: Created one broker");
    
    // create VM and cloudlet Lists
    val vmList = createVms(config.getInt("cloudSimulator.vm.numOfVMs"));
    logger.info("vmAllocationSim: Created list of vms");

    val cloudletList = createCloudletList(config.getInt("cloudSimulator.cloudlet.numOfCloudlets"),
      config.getDouble("cloudSimulator.utilizationRatio0"));
    logger.info("vmAllocationSim: Created list of cloudlets");
    
    // submits these lists to the broker
    broker0.submitVmList(vmList.asJava);
    logger.info("vmAllocationSim: Submitted vm list to broker");

    broker0.submitCloudletList(cloudletList.asJava);
    logger.info("vmAllocationSim: Submitted cloudlet list to broker");
    
    //start simulation
    logger.info("vmAllocationSim: Simulation about to start");
    cloudsim.start();
    new CloudletsTableBuilder(broker0.getCloudletFinishedList()).build();
    printTotalCost(broker0, datacenter0);
    printCpuUtilOfVms(broker0);



  
    



