package cn.sk.untils;

import org.jbpt.algo.tree.tctree.TCType;

public class RPSTParameter {


	//���岻ͬ�����������Ӧ����ֵ
		private int TRIVIAL_THRESHOLD;
		private int POLYGON_THRESHOLD;
		private int BOND_THRESHOLD;
		private int RIGID_THRESHOLD;
		private int UNDEFINED_THRESHOLD;
		/**
		 * 
		 * @param trivial
		 * @param polygon
		 * @param bond
		 * @param rigid
		 */
		public RPSTParameter(int trivial, int polygon, int bond, int rigid) {
			this.TRIVIAL_THRESHOLD = trivial;
			this.POLYGON_THRESHOLD = polygon;
			this.BOND_THRESHOLD = bond;
			this.RIGID_THRESHOLD = rigid;
			this.UNDEFINED_THRESHOLD = Integer.MAX_VALUE;
		}
		
		/**
		 * ��ȡָ�������������Ӧ����ֵ
		 * @param tc
		 * @return
		 */
		public int getThreshold(TCType tc){
			int threshold = UNDEFINED_THRESHOLD;
			if(tc.equals(TCType.TRIVIAL)){
				threshold = TRIVIAL_THRESHOLD;
			}else if(tc.equals(TCType.POLYGON)){
				threshold = POLYGON_THRESHOLD;
			}else if(tc.equals(TCType.BOND)){
				threshold = BOND_THRESHOLD;
			}else if(tc.equals(TCType.RIGID)){
				threshold = RIGID_THRESHOLD;
			}
			return threshold;
		}

}
