/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.core.CloudSim;

/**
 * VmAllocationPolicySimple is an VmAllocationPolicy that chooses, as the host for a VM, the host
 * with less PEs in use. It is therefore a Worst Fit policy, allocating VMs into the 
 * host with most available PE.
 * 
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
public class VmAllocationPolicyCustomized{
	private static boolean flag=false;
	private static ArrayList<Integer> maptemp=new ArrayList<Integer>();
	private static ArrayList<Vm> migrationVmList=new ArrayList<Vm>();
	private static ArrayList<Host> hotspotList= new ArrayList<Host>();
	private static ArrayList<Host> nothotspotList= new ArrayList<Host>();
	/*private static double max_util_cpu=0.0;
	private static double max_util_mem=0.0;
	private static double max_util_bw=0.0;
	private static double min_util_cpu=0.0;
	private static double min_util_mem=0.0;
	private static double min_util_bw=0.0;*/
	public VmAllocationPolicyCustomized(){
		//flag=true;
		//maptemp=null;
	}
	
	public ArrayList<Integer> allocationForMigration(List<Host> hostList, List<Vm> vmList){
		
		//HashMap<Integer,Integer> maptemp=new HashMap<Integer,Integer>();
		if (flag==false)
		{
			
			flag=true;
			System.out.println("flag value is " + flag);
			
			
			for (Host host : hostList) {
				List<Pe> peList=host.getPeList();
				int pemips=0;
				System.out.println("pelist size: "+peList.size());
				for(int i=0;i<peList.size();i++)
				{
					pemips=pemips+peList.get(i).getMips();
				}
				System.out.println("1: "+pemips);
				System.out.println("2: "+host.getMaxAvailableMips());
				System.out.println("3: "+host.getTotalMips());
				double max_util_cpu=host.getTotalMips()*0.8;
				double max_util_mem=host.getRam()*0.8;
				double max_util_bw=host.getBw()*0.8;
				double min_util_cpu=host.getTotalMips()*0.2;
				double min_util_mem=host.getRam()*0.2;
				double min_util_bw=host.getBw()*0.2;
				double mips=0.0;
				double ram=0;
				double bw=(long) 0.0;
			for (Vm vm : vmList) {
				
				if (host.getId()==vm.getHost().getId()) {
					mips=mips+vm.getTotalUtilizationOfCpuMips(CloudSim.clock());
					//System.out.println("mips of vm 1: "+vm.getCurrentAllocatedMips());
					System.out.println("mips of vm 2: "+vm.getTotalUtilizationOfCpuMips(CloudSim.clock()));
					ram=ram+vm.getCurrentAllocatedRam();
					bw=bw+vm.getCurrentAllocatedBw();
				}
			}	
			if (mips>max_util_cpu || ram>max_util_mem || bw>max_util_bw) {
				System.out.println("Maxed out");
				
				if (mips>max_util_cpu) {
					System.out.println("MIPS maxed");
				}
				System.out.println("host id of hotspot identified host is: "+host.getId());
				hotspotList.add(host);
			}
			else if (mips<min_util_cpu || ram<min_util_mem || bw<min_util_bw) {
				System.out.println("mips: "+mips);
				System.out.println("mips max: "+max_util_cpu);
				if (bw<min_util_bw) {
					System.out.println("MIPS mined out");
				}
				hotspotList.add(host);
			}
			else {
				nothotspotList.add(host);
			}
				
			}
			
			System.out.println("not hotspot list size: "+nothotspotList.size());
			System.out.println("hotspot list size: "+hotspotList.size());
			
			for (Host host : hotspotList) {
				long total= (long)0.0;
				List<Vm> vmlistofhost=host.getVmList();
				for (Vm vm:vmlistofhost) {
					total=(long) (total+vm.getTotalUtilizationOfCpuMips(CloudSim.clock())+vm.getCurrentAllocatedRam()+vm.getCurrentAllocatedBw());
				}
				long total2=(long) 0.0;
				for (Host temphost : hotspotList) {
					List<Vm> tempvmlistofhost=temphost.getVmList();
					if(host.getId()!=temphost.getId()) {
						for (Vm vm:tempvmlistofhost) {
							total2=(long) (total2+vm.getTotalUtilizationOfCpuMips(CloudSim.clock())+vm.getCurrentAllocatedRam()+vm.getCurrentAllocatedBw());
						}
						if(total2>total) {
							System.out.println("Sorting.........................");
							//Host temp=host;
							int index1=hotspotList.indexOf(host);
							int index2=hotspotList.indexOf(temphost);
							hotspotList.set(index1, temphost);
							hotspotList.set(index2, host);
						}
					}
				}	
			}
			
			for (Host host : nothotspotList) {
				long total= (long)0.0;
				List<Vm> vmlistofhost=host.getVmList();
				for (Vm vm:vmlistofhost) {
					total=(long) (total+vm.getTotalUtilizationOfCpuMips(CloudSim.clock())+vm.getCurrentAllocatedRam()+vm.getCurrentAllocatedBw());
				}
				long total2=(long) 0.0;
				for (Host temphost : nothotspotList) {
					List<Vm> tempvmlistofhost=temphost.getVmList();
					if(host.getId()!=temphost.getId()) {
						for (Vm vm:tempvmlistofhost) {
							total2=(long) (total+vm.getTotalUtilizationOfCpuMips(CloudSim.clock())+vm.getCurrentAllocatedRam()+vm.getCurrentAllocatedBw());
						}
						if(total2<total) {
							//Host temp=host;
							int index1=nothotspotList.indexOf(host);
							int index2=nothotspotList.indexOf(temphost);
							nothotspotList.set(index1, temphost);
							nothotspotList.set(index2, host);
						}
					}
				}	
			}
			
			for (Host host : hotspotList) {
				System.out.println("Hotspots found");
				System.out.println("Host id of hotspot host is: "+host.getId());
				List<Vm> tempvmlist = host.getVmList();
				List<Long> totalutilization=new ArrayList<Long>();
				for (Vm vm:tempvmlist) {
					long total= (long)0.0;
					total=(long) (total+vm.getTotalUtilizationOfCpuMips(CloudSim.clock())+vm.getCurrentAllocatedRam()+vm.getCurrentAllocatedBw());
					totalutilization.add(total);
				}
				for (int i=0;i<totalutilization.size();i++) {
					for (int j=0;j<totalutilization.size();j++) {
						if(totalutilization.get(i)<totalutilization.get(j)) {
							Vm vm1=tempvmlist.get(i);
							Vm vm2=tempvmlist.get(j);
							tempvmlist.set(i, vm2);
							tempvmlist.set(j, vm1);
						}
					}
				}	
				
				Vm vm=tempvmlist.get(0);
				double targetmips=vm.getTotalUtilizationOfCpuMips(CloudSim.clock());;
				int targetram=vm.getCurrentAllocatedRam();
				long targetbw=vm.getCurrentAllocatedBw();
				
				List<Double> totalutilizationdifference=new ArrayList<Double>();
				
				for (Host targethost:nothotspotList) {
					double targethost_max_cpu=targethost.getTotalMips()*0.8;
					double targethost_max_ram=targethost.getRam()*0.8;
					double targethost_max_bw=targethost.getBw()*0.8;
					double targethost_cpu=0.0;
					double targethost_ram=0.0;
					double targethost_bw=0.0;
					List<Vm> tagethostvmlist = targethost.getVmList();
					for (Vm targethostvm:tagethostvmlist) {
						targethost_cpu=targethost_cpu+targethostvm.getTotalUtilizationOfCpuMips(CloudSim.clock());
						targethost_ram=targethost_ram+targethostvm.getCurrentAllocatedRam();
						targethost_bw=targethost_bw+targethostvm.getCurrentAllocatedBw();
					}
				
					if ((targethost_cpu+targetmips)<targethost_max_cpu) {
						if ((targethost_ram+targetram)<targethost_max_ram) {
							if ((targethost_bw+targetbw)<targethost_max_bw) {
								double utilizationdifference=(targethost_cpu-targetmips)+(targethost_ram-targetram)+(targethost_bw-targetbw);
								totalutilizationdifference.add(utilizationdifference);
							}
						}
					}
				}
				
				double minimum_difference=9000000000000000000000000.0;
				int targethostindex=-1;
				for (int i=0;i<totalutilizationdifference.size();i++) {
					System.out.println("total util diff is: "+totalutilizationdifference.get(i));
					if (totalutilizationdifference.get(i)<minimum_difference) {
						minimum_difference=totalutilizationdifference.get(i);
						targethostindex=i;
						System.out.println("Success!");
					}
				}
				
				maptemp.add(vm.getId());
				maptemp.add(nothotspotList.get(targethostindex).getId());
				migrationVmList.add(vm);
			}
			
			/*for (Host host : nothotspotList) {
				
				List<Vm> vmlistofhost=host.getVmList();
				for (Vm vm:vmlistofhost) {
					maptemp.put(vm.getId(),host.getId());
				}
				
			}*/
			
			/*for (int i=0;i<vmList.size();i++){
				
				maptemp.put(i,1);
			}*/
			
			//return maptemp;
		}
		else if(flag==true)
		{
			System.out.println("No change");
			//return maptemp;
		}
		return maptemp;
		//return migrationVmList;
		//return null;
		
	}
}
