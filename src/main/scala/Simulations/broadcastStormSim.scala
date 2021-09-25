package Simulations

import HelperUtils.*
import HelperUtils.utils.*
import org.cloudbus.cloudsim.allocationpolicies.{VmAllocationPolicy, VmAllocationPolicySimple}
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.cloudlets.network.{CloudletExecutionTask, CloudletReceiveTask, CloudletSendTask, NetworkCloudlet}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.DatacenterSimple
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter
import org.cloudbus.cloudsim.hosts.{Host, HostSimple}
import org.cloudbus.cloudsim.hosts.network.NetworkHost
import org.cloudbus.cloudsim.network.{HostPacket, IcmpPacket, VmPacket, switches}
import org.cloudbus.cloudsim.network.switches.*
import org.cloudbus.cloudsim.network.topologies.BriteNetworkTopology
import org.cloudbus.cloudsim.provisioners.{PeProvisionerSimple, ResourceProvisionerSimple}
import org.cloudbus.cloudsim.resources.*
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull
import org.cloudbus.cloudsim.vms.Vm
import org.cloudbus.cloudsim.vms.network.NetworkVm
import org.cloudsimplus.builders.tables.CloudletsTableBuilder

import scala.language.postfixOps
import collection.JavaConverters.*

class broadcastStormSim
object broadcastStormSim:
  val config = ObtainConfigReference("cloudSimulator") match {
    case Some(value) => value
    case None => throw new RuntimeException("Cannot obtain a reference to the config data.")
  }
  val logger = CreateLogger(classOf[BasicCloudSimPlusExample])

  def Start() =
    val cloudSim = new CloudSim();
    val hostList = createHostList();
    val datacenter0 = createNetworkDatacenter(cloudSim, hostList);
    logger.info("Broadcast Storm: Created one datacenter");

    val broker0 = new DatacenterBrokerSimple(cloudSim);
    logger.info("Broadcast Storm: Created one broker");

    val vmList0 = createVms(datacenter0);
    logger.info("Broadcast Storm: Created list of vms");

    broker0.submitVmList(vmList0.asJava);
    logger.info("Broadcast Storm: Submitted vm list to broker");

    val cloudletList0 = createNetworkCloudlets(vmList0,hostList);
    broker0.submitCloudletList(cloudletList0.asJava);
    logger.info("Broadcast Storm: Submitted cloudlet list to broker");

    logger.info("Broadcast Storm: Similation about to start");
    cloudSim.start();
    new CloudletsTableBuilder(broker0.getCloudletFinishedList()).build();





  //creates two network hosts
  def createHostList(): List[NetworkHost] =
    val peList = createPEs(config.getInt("cloudSimulator.netHost.numOfPEs"),
      config.getLong("cloudSimulator.netHost.mipsCapacity"));
    val host1= new NetworkHost(config.getLong("cloudSimulator.netHost.RAMInMBs"),
      config.getLong("cloudSimulator.netHost.BandwidthInMBps"),
      config.getLong("cloudSimulator.netHost.StorageInMBs"), peList.asJava);
    host1
      .setRamProvisioner(new ResourceProvisionerSimple())
      .setBwProvisioner(new ResourceProvisionerSimple())
      .setVmScheduler(new VmSchedulerTimeShared());
    val host2= new NetworkHost(config.getLong("cloudSimulator.netHost.RAMInMBs"),
      config.getLong("cloudSimulator.netHost.BandwidthInMBps"),
      config.getLong("cloudSimulator.netHost.StorageInMBs"), peList.asJava);
    host2
    .setRamProvisioner(new ResourceProvisionerSimple())
    .setBwProvisioner(new ResourceProvisionerSimple())
    .setVmScheduler(new VmSchedulerTimeShared());
    return List(host1,host2);

  //creates one network datacenter
  def createNetworkDatacenter(cloudSim: CloudSim, hostList: List[NetworkHost]): NetworkDatacenter =
    val datacenter0 = new NetworkDatacenter(cloudSim, hostList.asJava, new VmAllocationPolicySimple());
    createNetwork(cloudSim, datacenter0, hostList);
    return datacenter0;

  //create PEs list
  def createPEs(numOfPes: Int, mips: Long): List[Pe] =
    val peList = List.fill(numOfPes)(new PeSimple(mips, new PeProvisionerSimple()));
    return peList;

  //creates network. host 0 is connected to switch0 and host1 is connected to switch1.
  //each switch is connected to the datacenter.
  def createNetwork(cloudSim: CloudSim, datacenter: NetworkDatacenter, hostList: List[NetworkHost]) =
    val edgeSwitch0 = new EdgeSwitch(cloudSim, datacenter);
    val edgeSwitch1 = new EdgeSwitch(cloudSim, datacenter);
    edgeSwitch0.setDownlinkBandwidth(config.getDouble("cloudSimulator.network.bandwidth"));
    edgeSwitch1.setDownlinkBandwidth(config.getDouble("cloudSimulator.network.bandwidth"));
    edgeSwitch0.setUplinkBandwidth(config.getDouble("cloudSimulator.network.bandwidth"));
    edgeSwitch1.setUplinkBandwidth(config.getDouble("cloudSimulator.network.bandwidth"));
    edgeSwitch0.setSwitchingDelay(config.getDouble("cloudSimulator.network.latency"));
    edgeSwitch1.setSwitchingDelay(config.getDouble("cloudSimulator.network.latency"));


    datacenter.addSwitch(edgeSwitch0);
    datacenter.addSwitch(edgeSwitch1);
    edgeSwitch0.connectHost(hostList(0));
    edgeSwitch1.connectHost(hostList(1));

  //creates 2 vms
  def createVms(datacenter: NetworkDatacenter): List[NetworkVm] =
    val vm1 = new NetworkVm(config.getInt("cloudSimulator.netVms.id0"), config.getLong("cloudSimulator.netHost.mipsCapacity"),
      config.getInt("cloudSimulator.netHost.numOfPEs"));
    vm1
      .setRam(config.getLong("cloudSimulator.netHost.RAMInMBs"))
      .setBw( config.getLong("cloudSimulator.netHost.BandwidthInMBps"))
      .setSize( config.getLong("cloudSimulator.netHost.StorageInMBs"))
      .setCloudletScheduler(new CloudletSchedulerTimeShared);
    val vm2 = new NetworkVm(1, config.getLong("cloudSimulator.netHost.mipsCapacity"),
      config.getInt("cloudSimulator.netHost.numOfPEs"));
    vm2
      .setRam(config.getLong("cloudSimulator.netHost.RAMInMBs"))
      .setBw( config.getLong("cloudSimulator.netHost.BandwidthInMBps"))
      .setSize( config.getLong("cloudSimulator.netHost.StorageInMBs"))
      .setCloudletScheduler(new CloudletSchedulerTimeShared);
    val vmList = List(vm1,vm2);

    return vmList;

  //creates two cloudlets and sends packets between cloudlets, hosts, and vms.
  def createNetworkCloudlets(vmList: List[NetworkVm], hostList: List[NetworkHost]): List[NetworkCloudlet] =
    val vm0 = vmList(0);
    val vm1 = vmList(1);
    val host0 = hostList(0);
    val host1 = hostList(1);
    val switch1 = host0.getEdgeSwitch();
    val switch2 = host1.getEdgeSwitch()
    val cloudletList = List(createCloudlet(vm0), createCloudlet(vm1));
    0.to(50).foreach(_=>

      //Send packets from host0 to host1
      val vmPacket1 = new VmPacket(vm0, vm1,
        config.getLong("cloudSimulator.netVms.packetSize"), cloudletList(0), cloudletList(1));
      val hostPacket1 = new HostPacket(host0, vmPacket1);
      host1.addReceivedNetworkPacket(hostPacket1);
      //host1.updateProcessing(config.getLong("cloudSimulator.vmCloudlet.sendTime"));

      //send packets from cloudlet0 to cloudlet1
      addExecutionTask(cloudletList(0));
      addSendTask(cloudletList(0), cloudletList(1));
      addReceiveTask(cloudletList(1), cloudletList(0));
      addExecutionTask((cloudletList(1)));

      //send packets from host1 to host0
      val vmPacket2 = new VmPacket(vm1, vm0,
        config.getLong("cloudSimulator.netVms.packetSize"), cloudletList(1), cloudletList(0));
      val hostPacket2 = new HostPacket(host1, vmPacket2);
      host0.addReceivedNetworkPacket(hostPacket2);
      //host0.updateProcessing(config.getLong("cloudSimulator.vmCloudlet.sendTime"));

      //send packets from cloudlet1 to cloudlet0
      addExecutionTask(cloudletList(1));
      addSendTask(cloudletList(1), cloudletList(0));
      addReceiveTask(cloudletList(0), cloudletList(1));
      addExecutionTask((cloudletList(0)));

      //send icmp packet between switches
      sendIcmp(switch1, switch2);
      sendIcmp(switch2, switch1);
     );
    return cloudletList;

  //create one cloudlet with full utilization
  def createCloudlet(vm: NetworkVm): NetworkCloudlet =
    val cloudlet = new NetworkCloudlet(config.getLong("cloudSimulator.vmCloudlet.length"), config.getInt("cloudSimulator.netHost.numOfPEs"));
    cloudlet
      .setMemory(config.getLong("cloudSimulator.vmCloudlet.RAMInMBs"))
      .setFileSize(config.getLong("cloudSimulator.vmCloudlet.size"))
      .setOutputSize(config.getLong("cloudSimulator.vmCloudlet.size"))
      .setUtilizationModel(new UtilizationModelFull())
      .setVm(vm)
      .setBroker(vm.getBroker());
    return cloudlet;

  //adds task for cloudlets
  def addExecutionTask(cloudlet: NetworkCloudlet) =
    val task = new CloudletExecutionTask(cloudlet.getTasks().size(), config.getLong("cloudSimulator.vmCloudlet.length"));
    task.setMemory(config.getLong("cloudSimulator.vmCloudlet.RAMInMBs"));
    cloudlet.addTask(task);

  //sets the cloudlet that sends the packet
  def addSendTask(source: NetworkCloudlet, dest: NetworkCloudlet) =
    val task = new CloudletSendTask(source.getTasks().size());
    task.setMemory(config.getLong("cloudSimulator.vmCloudlet.RAMInMBs"));
    source.addTask(task);
    task.addPacket(dest, config.getLong("cloudSimulator.netVms.packetSize"));

  //sets the cloudlet that recieves the task
  def addReceiveTask(cloudlet: NetworkCloudlet, source: NetworkCloudlet) =
    val task = new CloudletReceiveTask(cloudlet.getTasks().size(), source.getVm());
    task.setMemory(config.getLong("cloudSimulator.vmCloudlet.RAMInMBs"));
    task.setExpectedPacketsToReceive(config.getLong("cloudSimulator.vmCloudlet.expectedPackets"));
    cloudlet.addTask(task);

  //send a icmp ping between two switches
  def sendIcmp(source: Switch, dest:  Switch): IcmpPacket =
    val icmp = new IcmpPacket("icmp: ", config.getInt("cloudSimulator.netVms.id0"),
      config.getLong("cloudSimulator.netVms.packetSize"), source, dest, config.getInt("cloudSimulator.netVms.id0"));
    icmp.setSendTime(config.getLong("cloudSimulator.vmCloudlet.sendTime"));
    icmp.setReceiveTime(config.getLong("cloudSimulator.vmCloudlet.receiveTime"));
    return icmp;











