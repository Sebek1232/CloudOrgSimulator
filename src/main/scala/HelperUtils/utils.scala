package HelperUtils

import Simulations.BasicCloudSimPlusExample
import org.cloudbus.cloudsim.allocationpolicies.*
import org.cloudbus.cloudsim.brokers.DatacenterBroker
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.{Datacenter, DatacenterSimple}
import org.cloudbus.cloudsim.hosts.HostSimple
import org.cloudbus.cloudsim.network.IcmpPacket
import org.cloudbus.cloudsim.network.topologies.{BriteNetworkTopology, NetworkTopology}
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple
import org.cloudbus.cloudsim.resources.PeSimple
import org.cloudbus.cloudsim.schedulers.cloudlet.*
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic
import org.cloudbus.cloudsim.vms.{Vm, VmCost, VmSimple}

import collection.JavaConverters.*

object utils:
  val config = ObtainConfigReference("cloudSimulator") match {
    case Some(value) => value
    case None => throw new RuntimeException("Cannot obtain a reference to the config data.")
  }
  val logger = CreateLogger(classOf[BasicCloudSimPlusExample])

  //returns a DatacenterSimple object. Creates hostlist with number of hosts passed in with 
  //passed in number of pes. Then that list passed into the datacenter constructor. With
  //the passed in allocation policy and cloud simulation. 
  def createDatacenter(cloudSim: CloudSim, numOfHosts: Int, numOfPEs: Int, vmAlloPolicy: VmAllocationPolicy): Datacenter = {
    val hostList = List.fill(numOfHosts)(createHost(numOfPEs));
    return new DatacenterSimple(cloudSim, hostList.asJava, vmAlloPolicy);
  }

  //returns a DatacenterSimple object. Creates hostlist with number of hosts passed in with 
  //passed in number of pes. The created hosts have Time-Shared Vm Scehduler. Then that list passed into 
  // the datacenter constructor. With the passed in allocation policy and cloud simulation. 
  def createDatacenterWithTimeShared(cloudSim: CloudSim, numOfHosts: Int, numOfPEs: Int, vmAlloPolicy: VmAllocationPolicy): Datacenter = {
    val hostList = List.fill(numOfHosts)(createHostWithTimeShared(numOfPEs));
    return new DatacenterSimple(cloudSim, hostList.asJava, vmAlloPolicy);
  }

  //Creates and returns a HostSimple object with the specified number of PEs
  def createHost(numOfPEs: Int): HostSimple =
    val hostPes = List.fill(numOfPEs)(new PeSimple(config.getLong("cloudSimulator.host.mipsCapacity")));
    return new HostSimple(config.getLong("cloudSimulator.host.RAMInMBs"),
      config.getLong("cloudSimulator.host.StorageInMBs"),
      config.getLong("cloudSimulator.host.BandwidthInMBps"),
      hostPes.asJava);

  //Creates and returns a HostSimple object with the specified number of PEs and a Time-Shared Vm Scehduler
  def createHostWithTimeShared(numOfPEs: Int): HostSimple =
    val hostPes = List.fill(numOfPEs)(new PeSimple(config.getLong("cloudSimulator.host.mipsCapacity")));
    val ramProvisioner = new ResourceProvisionerSimple();
    val bwProvisioner = new ResourceProvisionerSimple();
    val vmScheduler = new VmSchedulerTimeShared();
    val host = new HostSimple(config.getLong("cloudSimulator.host.RAMInMBs"),
      config.getLong("cloudSimulator.host.StorageInMBs"),
      config.getLong("cloudSimulator.host.BandwidthInMBps"),
      hostPes.asJava)
    host
      .setRamProvisioner(ramProvisioner)
      .setBwProvisioner(bwProvisioner)
      .setVmScheduler(vmScheduler);
    return host

  //Creates and returns a list of Vms. The list size is passed.
  def createVms(numOfVMs: Int): List[Vm] =
    val vmList = List.fill(numOfVMs)(new VmSimple(config.getLong("cloudSimulator.vm.mipsCapacity"),
      config.getLong("cloudSimulator.vm.numOfPEs"))
      .setRam(config.getLong("cloudSimulator.vm.RAMInMBs"))
      .setBw(config.getLong("cloudSimulator.vm.BandwidthInMBps"))
      .setSize(config.getLong("cloudSimulator.vm.StorageInMBs")));
    vmList.foreach(vm => vm.enableUtilizationStats());
    return vmList;

  //Creates and returns a list of Vms. The list size is passed. Sets a cloudlet space-shared scheduler
  def createVmsWithSpaceShared(numOfVMs: Int): List[Vm] =
    val vmList = List.fill(numOfVMs)(new VmSimple(config.getLong("cloudSimulator.vm.mipsCapacity"),
      config.getLong("cloudSimulator.vm.numOfPEs"))
      .setRam(config.getLong("cloudSimulator.vm.RAMInMBs"))
      .setBw(config.getLong("cloudSimulator.vm.BandwidthInMBps"))
      .setSize(config.getLong("cloudSimulator.vm.StorageInMBs"))
      .setCloudletScheduler(new CloudletSchedulerSpaceShared()));
    vmList.foreach(vm => vm.enableUtilizationStats());
    return vmList;

  //Creates and returns a list of Vms. The list size is passed. Sets a cloudlet time-shared scheduler
  def createVmsWithTimeShared(numOfVMs: Int): List[Vm] =
    val vmList = List.fill(numOfVMs)(new VmSimple(config.getLong("cloudSimulator.vm.mipsCapacity"),
      config.getLong("cloudSimulator.vm.numOfPEs"))
      .setRam(config.getLong("cloudSimulator.vm.RAMInMBs"))
      .setBw(config.getLong("cloudSimulator.vm.BandwidthInMBps"))
      .setSize(config.getLong("cloudSimulator.vm.StorageInMBs"))
      .setCloudletScheduler(new CloudletSchedulerCompletelyFair()));
    vmList.foreach(vm => vm.enableUtilizationStats());
    return vmList;

  //creates and returns a cloudlet list witg a dynamic utilization model
  def createCloudletList(numOfCloudlets: Int, utilizationPercentage: Double): List[Cloudlet] =
    val utilizationModel = new UtilizationModelDynamic(utilizationPercentage);

    val cloudletList = List.fill(numOfCloudlets)(new CloudletSimple(config.getLong("cloudSimulator.cloudlet.size"),
      config.getInt("cloudSimulator.cloudlet.PEs"), utilizationModel)
      .setUtilizationModelCpu(utilizationModel));
    return cloudletList;

  //sets cost values for a passed in datacenter. These costs are arbitary. Can be any currency
  def setCostValues(datacenter: Datacenter) =
    datacenter.getCharacteristics()
      .setCostPerSecond(config.getDouble("cloudSimulator.costs.costPerSec"))
      .setCostPerMem(config.getDouble("cloudSimulator.costs.costPerMem"))
      .setCostPerStorage(config.getDouble("cloudSimulator.costs.costPerStorage"))
      .setCostPerBw(config.getDouble("cloudSimulator.costs.costPerBw"));
  

  //Prints the min/max/avg cpu utilization of each vm. 
  def printCpuUtilOfVms(broker: DatacenterBroker) =
    val vmList = broker.getVmCreatedList().asScala;
    vmList.foreach(vm =>
      val cpuUsageMin = vm.getCpuUtilizationStats().getMin()*100;
      val cpuUsageAvg = vm.getCpuUtilizationStats().getMean()*100;
      val cpuUsageMax = vm.getCpuUtilizationStats().getMax()*100;
      val vmId = vm.getId();
      logger.info(s"Vm $vmId: Cpu Utilization: Min:$cpuUsageMin Avg:$cpuUsageAvg Max:$cpuUsageMax");
      );

  //Prints the total cost specified by the setCostValues to run a datacenter 
  def printTotalCost(broker: DatacenterBroker, datacenter: Datacenter) =
    var totalCost = 0.0;
    var totalNonIdleVms = 0.0;
    var processingTotalCost = 0.0;
    var memoryTotalCost = 0.0;
    var storageTotalCost = 0.0;
    var bwTotalCost = 0.0;
    val datacenterId = datacenter.getId();
    broker.getVmCreatedList.asScala.foreach(vm =>
      var cost = new VmCost(vm);
      processingTotalCost += cost.getProcessingCost();
      memoryTotalCost += cost.getMemoryCost();
      storageTotalCost += cost.getStorageCost();
      bwTotalCost += cost.getBwCost();
      totalCost += cost.getTotalCost();
      if (vm.getTotalExecutionTime > 1)
        totalNonIdleVms += 1;)
    logger.info(s"Total Costs for Datacenter $datacenterId");
    logger.info(s"Total Cost: $totalCost, Total Processing Cost: $processingTotalCost, Total Memory Cost: $memoryTotalCost");
    logger.info(s"Total Storage Cost: $storageTotalCost, Total Monetary Cost: $bwTotalCost, Total Non Idle VMs: $totalNonIdleVms");
