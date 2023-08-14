package cn.sk.RPSTMethod;

import java.util.List;

import org.processmining.framework.log.LogEvents;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

import cern.colt.matrix.DoubleMatrix2D;
import cn.sk.untils.AndOrAnalysisParameters;


/**
 * 分支的AND/OR关系挖掘
 * @author cannyking
 *
 */

public class AndOrAnalysis {
	

	
	
	
	private LogEvents logEvents;
	private DoubleMatrix2D directSuccession;
	private AndOrAnalysisParameters parameters;
	
	public AndOrAnalysis(LogEvents logEvents, 
			DoubleMatrix2D directSuccession,
			AndOrAnalysisParameters parameters) {
		this.logEvents = logEvents;
		this.directSuccession = directSuccession;
		this.parameters = parameters;
	}
	
	/**
	 * 分析变迁owner之前的变迁newTran与oldTrans是否都是OR关系
	 * @param owner
	 * @param list
	 * @param newTran
	 * @return
	 */
	public boolean allIsOrAnalysis_In(Transition owner, 
			List<Transition> list, Transition newTran) {
		for (Transition oldTran : list) {
			if (!isOrAnalysis_In(owner, oldTran, newTran)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 分析变迁owner之后的变迁newTran与oldTrans是否都是OR关系
	 * @param comTransition
	 * @param list
	 * @param newTran
	 * @return
	 */
	public boolean allIsOrAnalysis_Out(Transition comTransition, 
			List<Transition> list, Transition newTran) {
		for (Transition oldTran : list) {
			if (!isOrAnalysis_Out(comTransition, oldTran, newTran)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 分析变迁owner之前的变迁newTran与oldTran是否是OR关系
	 * @param owner
	 * @param oldTran
	 * @param newTran
	 * @return
	 */
	public boolean isOrAnalysis_In(Transition owner, 
			Transition oldTran, Transition newTran) {
		
		int ownerId = getLogEventIndex(owner);
		int oldId = getLogEventIndex(oldTran);
		int newId = getLogEventIndex(newTran);
		if(ownerId!=-1&&oldId!=-1&&newId!=-1) {
			if (andMeasure_In(ownerId, oldId, newId) > parameters.getAndThreshold()) {
				return false;
			}
		}
		if(ownerId==-1||oldId==-1||newId==-1) {
			//return true;//合理性保证
			return false;
		}
		
		return true;
	}
	
	private int getLogEventIndex(Transition owner) {
		int max = logEvents.size();
		for(int i = 0;i<max;i++) {
			String name = logEvents.get(i).getModelElementName();
			if(name.equals(owner.getLabel())) {
				
				return i;
			}
		}
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 分析变迁owner之后的变迁newTran与oldTran是否是OR关系
	 * @param owner
	 * @param oldTran
	 * @param newTran
	 * @return
	 */
	public boolean isOrAnalysis_Out(Transition owner, 
			Transition oldTran, Transition newTran) {
		int ownerId = getLogEventIndex(owner);
		int oldId = getLogEventIndex(oldTran);
		int newId = getLogEventIndex(newTran);
		//System.out.println("ownerId索引是"+ownerId+"oldId索引是"+oldId+"newId索引是"+newId);
		if(ownerId!=-1&&oldId!=-1&&newId!=-1) {
			if (andMeasure_Out(ownerId, oldId, newId) > parameters.getAndThreshold()) {
				return false;
			}
		}
		if(ownerId==-1&&oldId==-1&&newId==-1) {
			//return true;//合理性保证
			return false;
		}
		return true;
	}
	
	/**
	 * 计算变迁owner之前的变迁newTran与oldTran的并发值
	 * @param ownerId
	 * @param oldId
	 * @param newId
	 * @return
	 */
	private double andMeasure_In(int ownerId, int oldId, int newId) {
		if (ownerId == newId) {
			return 0.0;
		}else if (directSuccession.get(oldId, newId) < parameters.getPositiveObservations()
				|| directSuccession.get(newId, oldId) < parameters.getPositiveObservations()) {
			return 0.0;
		}else {
			return ((double) directSuccession.get(oldId, newId) 
					+ directSuccession.get(newId, oldId))
				/
				(directSuccession.get(oldId, ownerId) 
						+ directSuccession.get(newId, ownerId)
						+ parameters.getDependencyDivisor());
		}
	}
	
	/**
	 * 计算变迁owner之后的变迁newTran与oldTran的并发值
	 * @param ownerId
	 * @param oldId
	 * @param newId
	 * @return
	 */
	private double andMeasure_Out(int ownerId, int oldId, int newId) {
		if (ownerId == newId) {
			return 0.0;
		}else if (directSuccession.get(oldId, newId) < parameters.getPositiveObservations()
				|| directSuccession.get(newId, oldId) < parameters.getPositiveObservations()) {
			return 0.0;
		}else {
			return ((double) directSuccession.get(oldId, newId) 
						+ directSuccession.get(newId, oldId))
					/
					(directSuccession.get(ownerId, oldId) 
							+ directSuccession.get(ownerId, newId)
							+ parameters.getDependencyDivisor());
		}
	}
	
	
	
	
	
	
	
	
	
}
