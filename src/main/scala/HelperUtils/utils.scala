package HelperUtils

import Simulations.BasicCloudSimPlusExample
import org.cloudbus.cloudsim.allocationpolicies.*
import org.cloudbus.cloudsim.brokers.DatacenterBroker
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.{Datacenter, DatacenterSimple}
import org.cloudbus.cloudsim.hosts.HostSimple
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

  def createDatacenter(cloudSim: CloudSim, numOfHosts: Int, numOfPEs: Int, vmAlloPolicy: VmAllocationPolicy): Datacenter = {
    val hostList = List.fill(numOfHosts)(createHost(numOfPEs));
    return new DatacenterSimple(cloudSim, hostList.asJava, vmAlloPolicy);
  }

  def createDatacenterWithTimeShared(cloudSim: CloudSim, numOfHosts: Int, numOfPEs: Int, vmAlloPolicy: VmAllocationPolicy): Datacenter = {
    val hostList = List.fill(numOfHosts)(createHostWithTimeShared(numOfPEs));
    return new DatacenterSimple(cloudSim, hostList.asJava, vmAlloPolicy);
  }

  def createHost(numOfPEs: Int): HostSimple =
    val hostPes = List.fill(numOfPEs)(new PeSimple(config.getLong("cloudSimulator.host.mipsCapacity")));
    return new HostSimple(config.getLong("cloudSimulator.host.RAMInMBs"),
      config.getLong("cloudSimulator.host.StorageInMBs"),
      config.getLong("cloudSimulator.host.BandwidthInMBps"),
      hostPes.asJava)


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

  def createVms(numOfVMs: Int): List[Vm] =
    val vmList = List.fill(numOfVMs)(new VmSimple(config.getLong("cloudSimulator.vm.mipsCapacity"),
      config.getLong("cloudSimulator.vm.numOfPEs"))
      .setRam(config.getLong("cloudSimulator.vm.RAMInMBs"))
      .setBw(config.getLong("cloudSimulator.vm.BandwidthInMBps"))
      .setSize(config.getLong("cloudSimulator.vm.StorageInMBs")));
    return vmList;

  def createVmsWithSpaceShared(numOfVMs: Int): List[Vm] =
    val vmList = List.fill(numOfVMs)(new VmSimple(config.getLong("cloudSimulator.vm.mipsCapacity"),
      config.getLong("cloudSimulator.vm.numOfPEs"))
      .setRam(config.getLong("cloudSimulator.vm.RAMInMBs"))
      .setBw(config.getLong("cloudSimulator.vm.BandwidthInMBps"))
      .setSize(config.getLong("cloudSimulator.vm.StorageInMBs"))
      .setCloudletScheduler(new CloudletSchedulerSpaceShared()));
    return vmList;

  def createVmsWithTimeShared(numOfVMs: Int): List[Vm] =
    val vmList = List.fill(numOfVMs)(new VmSimple(config.getLong("cloudSimulator.vm.mipsCapacity"),
      config.getLong("cloudSimulator.vm.numOfPEs"))
      .setRam(config.getLong("cloudSimulator.vm.RAMInMBs"))
      .setBw(config.getLong("cloudSimulator.vm.BandwidthInMBps"))
      .setSize(config.getLong("cloudSimulator.vm.StorageInMBs"))
      .setCloudletScheduler(new CloudletSchedulerTimeShared()));
    return vmList;

  def createCloudletList(numOfCloudlets: Int): List[Cloudlet] =
    val utilizationModel = new UtilizationModelDynamic(config.getDouble("cloudSimulator.utilizationRatio"));
    val cloudletList = List.fill(numOfCloudlets)(new CloudletSimple(config.getLong("cloudSimulator.cloudlet.size"), config.getInt("cloudSimulator.cloudlet.PEs"), utilizationModel));
    return cloudletList;

  def setCostValues(datacenter: Datacenter) =
    datacenter.getCharacteristics()
      .setCostPerSecond(config.getDouble("cloudSimulator.costs.costPerSec"))
      .setCostPerMem(config.getDouble("cloudSimulator.costs.costPerMem"))
      .setCostPerStorage(config.getDouble("cloudSimulator.costs.costPerStorage"))
      .setCostPerBw(config.getDouble("cloudSimulator.costs.costPerBw"));

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
