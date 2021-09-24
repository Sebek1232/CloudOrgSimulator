package Simulations

import Simulations.BasicCloudSimPlusExample.config
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ConfigTests extends AnyFlatSpec with Matchers {
  behavior of "configuration parameters module"

  it should "obtain the utilization ratio 0" in {
    config.getDouble("cloudSimulator.utilizationRatio0") shouldBe .5
  }
  it should "obtain utilization ratio 1" in {
    config.getDouble("cloudSimulator.utilizationRatio1") shouldBe .25
  }
  it should "obtain utilization ratio 2" in {
    config.getDouble("cloudSimulator.utilizationRatio2") shouldBe 1.0
  }

  it should "obtain host0" in {
    config.getLong("cloudSimulator.host0.numOfPEs") shouldBe 8
    config.getLong("cloudSimulator.host0.mipsCapacity") shouldBe 2000
    config.getLong("cloudSimulator.host0.RAMInMBs") shouldBe 2000
    config.getLong("cloudSimulator.host0.StorageInMBs") shouldBe 2000
    config.getLong("cloudSimulator.host0.BandwidthInMBps") shouldBe 10000
  }

  it should "obtain host1" in {
    config.getLong("cloudSimulator.host1.numOfPEs") shouldBe 16
    config.getLong("cloudSimulator.host1.mipsCapacity") shouldBe 4000
    config.getLong("cloudSimulator.host1.RAMInMBs") shouldBe 4000
    config.getLong("cloudSimulator.host1.StorageInMBs") shouldBe 4000
    config.getLong("cloudSimulator.host0.BandwidthInMBps") shouldBe 10000
  }

  it should "obtain host2" in {
    config.getLong("cloudSimulator.host2.numOfPEs") shouldBe 8
    config.getLong("cloudSimulator.host2.mipsCapacity") shouldBe 20000
    config.getLong("cloudSimulator.host2.RAMInMBs") shouldBe 10000
    config.getLong("cloudSimulator.host2.StorageInMBs") shouldBe 100000
    config.getLong("cloudSimulator.host2.BandwidthInMBps") shouldBe 100000
  }

  it should "obtain general host" in {
    config.getLong("cloudSimulator.host.numOfHosts") shouldBe 1
    config.getLong("cloudSimulator.host.numOfPEs") shouldBe 4
    config.getLong("cloudSimulator.host.mipsCapacity") shouldBe 8000
    config.getLong("cloudSimulator.host.RAMInMBs") shouldBe 8000
    config.getLong("cloudSimulator.host.StorageInMBs") shouldBe 8000
    config.getLong("cloudSimulator.host.BandwidthInMBps") shouldBe 8000
  }

  it should "obtain vm" in {
    config.getLong("cloudSimulator.vm.numOfVMs") shouldBe 8
    config.getLong("cloudSimulator.vm.numOfPEs") shouldBe 4
    config.getLong("cloudSimulator.vm.mipsCapacity") shouldBe 1000
    config.getLong("cloudSimulator.vm.RAMInMBs") shouldBe 1000
    config.getLong("cloudSimulator.vm.StorageInMBs") shouldBe 1000
    config.getLong("cloudSimulator.vm.BandwidthInMBps") shouldBe 1000
  }

  it should "obtain costs" in {
    config.getDouble("cloudSimulator.costs.costPerSec") shouldBe 0.01
    config.getDouble("cloudSimulator.costs.costPerMem") shouldBe 0.02
    config.getDouble("cloudSimulator.costs.costPerStorage") shouldBe 0.001
    config.getDouble("cloudSimulator.costs.costPerBw") shouldBe 0.005
  }

  it should "obtain netHost" in {
    config.getInt("cloudSimulator.netHost.numOfPEs") shouldBe 4
    config.getLong("cloudSimulator.netHost.mipsCapacity") shouldBe 1000
    config.getLong("cloudSimulator.netHost.RAMInMBs") shouldBe 2048
    config.getLong("cloudSimulator.netHost.StorageInMBs") shouldBe 1000000
    config.getLong("cloudSimulator.netHost.BandwidthInMBps") shouldBe 10000
  }

  it should "obtain network" in {
    config.getInt("cloudSimulator.network.bandwidth") shouldBe 100
    config.getLong("cloudSimulator.network.latency") shouldBe 50
  }
  it should "obtain vmCloudlet" in {
    config.getLong("cloudSimulator.vmCloudlet.RAMInMBs") shouldBe 100
    config.getLong("cloudSimulator.vmCloudlet.length") shouldBe 400
    config.getLong("cloudSimulator.vmCloudlet.size") shouldBe 300
  }


}
