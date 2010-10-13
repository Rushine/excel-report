package reporting_tool;

import java.util.ArrayList;
import java.util.ListIterator;

public class Summary {

	public class SummaryElement {
		private String name;
		private String path;
		private ArrayList<Long>  cycles;
		private ArrayList<Long>  sizes_obj;
		private ArrayList<Long>  sizes_bin;
		private ArrayList<Long>  sizes_func;
		private ArrayList<Long>  NW_cycles;
		private ArrayList<Long>  CO_cycles;
		private ArrayList<Long>  BT_cycles;
		private ArrayList<Long>  CSD_cycles;
		private ArrayList<Long>  JPEG_cycles;
		private ArrayList<Long>  NW_sizes_obj;
		private ArrayList<Long>  CO_sizes_obj;
		private ArrayList<Long>  BT_sizes_obj ;
		private ArrayList<Long>  CSD_sizes_obj;
		private ArrayList<Long>  JPEG_sizes_obj;
		private ArrayList<Long>  NW_sizes_bin;
		private ArrayList<Long>  CO_sizes_bin;
		private ArrayList<Long>  BT_sizes_bin;
		private ArrayList<Long>  CSD_sizes_bin;
		private ArrayList<Long>  JPEG_sizes_bin;
		private ArrayList<Long>  NW_sizes_func;
		private ArrayList<Long>  CO_sizes_func;
		private ArrayList<Long>  BT_sizes_func;
		private ArrayList<Long>  CSD_sizes_func;
		private ArrayList<Long>  JPEG_sizes_func;

		public SummaryElement(String path,String name) {
			this.path = path;
			this.name = name;
			this.cycles = new ArrayList<Long>();
			this.NW_cycles = new ArrayList<Long>();
			this.CO_cycles = new ArrayList<Long>();
			this.BT_cycles = new ArrayList<Long>();
			this.CSD_cycles = new ArrayList<Long>();
			this.JPEG_cycles = new ArrayList<Long>();
			this.sizes_obj = new ArrayList<Long>();
			this.sizes_bin = new ArrayList<Long>();
			this.sizes_func = new ArrayList<Long>();
			this.NW_sizes_obj=new ArrayList<Long>();
			this.CO_sizes_obj=new ArrayList<Long>();
			this.BT_sizes_obj=new ArrayList<Long>();
			this.CSD_sizes_obj=new ArrayList<Long>();
			this.JPEG_sizes_obj =new ArrayList<Long>();
			this.NW_sizes_bin =new ArrayList<Long>();
			this.CO_sizes_bin =new ArrayList<Long>();
			this.BT_sizes_bin =new ArrayList<Long>();
			this.CSD_sizes_bin =new ArrayList<Long>();
			this.JPEG_sizes_bin =new ArrayList<Long>();
			this.NW_sizes_func =new ArrayList<Long>();
			this.CO_sizes_func =new ArrayList<Long>();
			this.BT_sizes_func =new ArrayList<Long>();
			this.CSD_sizes_func =new ArrayList<Long>();
			this.JPEG_sizes_func =new ArrayList<Long>();
		}

		String get_name() {return name;}
		String get_path() {return path;}

		void add_cycle(String tname, long l) {
			boolean is_ext=CommonData.is_extension_info(tname);
			if (sqa_report.core_only && is_ext)  return;
			if (sqa_report.ext_only && !is_ext)  return;
			cycles.add(l);
			if (tname.contains("eembc_networking")) NW_cycles.add(l);
			if (tname.contains("eembc_consumer")) CO_cycles.add(l);
			if (tname.contains("bluetooth")) BT_cycles.add(l);
			if (tname.contains("SRC") || tname.contains("IDCT") || tname.contains("Second order IIR") 
					|| tname.contains("IFFT") || tname.contains("Huffman decoding")) CSD_cycles.add(l);
			if (tname.contains("JPEG Decoder CSD")) JPEG_cycles.add(l);
		}
		void add_bin_size(String tname, Long val){
			boolean is_ext=CommonData.is_extension_info(tname);
			if (sqa_report.core_only && is_ext)  return;
			if (sqa_report.ext_only && !is_ext)  return;
			sizes_bin.add(val);
			if (tname.contains("eembc_networking")) NW_sizes_bin.add(val);
			if (tname.contains("eembc_consumer")) CO_sizes_bin.add(val);
			if (tname.contains("bluetooth")) BT_sizes_bin.add(val);
			if (tname.contains("audio_csd")) CSD_sizes_bin.add(val);
			if (tname.contains("jpeg_csd")) JPEG_sizes_bin.add(val);

		}
		void add_obj_size(String tname, Long val){
			boolean is_ext=CommonData.is_extension_info(tname);
			if (sqa_report.core_only && is_ext)  return;
			if (sqa_report.ext_only && !is_ext)  return;
			sizes_obj.add(val);
			if (tname.contains("eembc_networking")) NW_sizes_obj.add(val);
			if (tname.contains("eembc_consumer")) CO_sizes_obj.add(val);
			if (tname.contains("bluetooth")) BT_sizes_obj.add(val);
			if (tname.contains("audio_csd")) CSD_sizes_obj.add(val);
			if (tname.contains("jpeg_csd")) JPEG_sizes_obj.add(val);

		}
		void add_func_size(String tname, Long val){
			boolean is_ext=CommonData.is_extension_info(tname);
			if (sqa_report.core_only && is_ext)  return;
			if (sqa_report.ext_only && !is_ext)  return;
			sizes_func.add(val);
			if (tname.contains("eembc_networking")) NW_sizes_func.add(val);
			if (tname.contains("eembc_consumer")) CO_sizes_func.add(val);
			if (tname.contains("bluetooth")) BT_sizes_func.add(val);
			if (tname.contains("audio_csd")) CSD_sizes_func.add(val);
			if (tname.contains("jpeg_csd")) JPEG_sizes_func.add(val);			
		}
		String get_cc_name(){
			String result = path.substring(0,path.lastIndexOf("/"));
			return result.substring(0, result.lastIndexOf("/"));
		}

		ArrayList<Long> get_cycles(CommonData.Dump_Type type){
			switch (type) {
			case Run_Valid :  	return cycles;
			case EEMBC_Net :	return NW_cycles;
			case EEMBC_Cons :	return CO_cycles;
			case Bluetooth :	return BT_cycles;
			case Jpeg :			return JPEG_cycles;
			case Audio_CSD :	return CSD_cycles;
			}
			return null;

		}
		ArrayList<Long> get_size(CommonData.Dump_Type type, Discriminent disc) {
			if(disc == Discriminent.SIZE_OBJ) {
				switch (type) {
				case Run_Valid : 	return sizes_obj;
				case EEMBC_Net :	return NW_sizes_obj;
				case EEMBC_Cons :	return CO_sizes_obj;
				case Bluetooth :	return BT_sizes_obj;
				case Jpeg :			return JPEG_sizes_obj;
				case Audio_CSD :	return CSD_sizes_obj;
				}	
			} 
			if(disc == Discriminent.SIZE_BIN) {
				switch (type) {
				case Run_Valid :	return sizes_bin;
				case EEMBC_Net :	return NW_sizes_bin;
				case EEMBC_Cons :	return CO_sizes_bin;
				case Bluetooth :	return BT_sizes_bin;
				case Jpeg :			return JPEG_sizes_bin;
				case Audio_CSD :	return CSD_sizes_bin;
				}
			}
			if(disc == Discriminent.SIZE_FUNC) {
				switch (type) {
				case Run_Valid :	return sizes_func;
				case EEMBC_Net :	return NW_sizes_func;
				case EEMBC_Cons :	return CO_sizes_func;
				case Bluetooth :	return BT_sizes_func;
				case Jpeg :			return JPEG_sizes_func;
				case Audio_CSD :	return CSD_sizes_func;
				}
			}
			return null;
		}

	}

	private ArrayList<SummaryElement> elements;
	public Summary() {
		elements = new ArrayList<SummaryElement>();
	}

	public void add_session(String  path, String name) {
		SummaryElement my_elem = new SummaryElement(path,name);
		elements.add(my_elem);
	}

	public void add_summary_value(String path, String name, String tname, Discriminent type, long l) {
		for (int i=0 ; i<elements.size(); i++) {
			if (elements.get(i).get_name().contentEquals(name) && elements.get(i).get_path().contentEquals(path)){
				if (type==Discriminent.SPEED) elements.get(i).add_cycle(tname,l);
				if (type==Discriminent.SIZE_OBJ) elements.get(i).add_obj_size(tname,l);
				if (type==Discriminent.SIZE_BIN) elements.get(i).add_bin_size(tname,l);
				if (type==Discriminent.SIZE_FUNC) elements.get(i).add_func_size(tname,l);
				return ;
			}
		}
	}

	
//	<property name="O3MultCAS_ref_compiler" value="23251"/>
//	<property name="O3MultCAS_ref_cycles" value=" Max Gain: 0.000, Max Loss: 0.000, Geomean: 0.000" />
//	<property name="O3MultCAS_ref_obj_size" value=" Max Gain: 0.000, Max Loss: 0.000, Average: 0.000" />
//	<property name="O3MultCAS_ref_bin_size" value=" Max Gain: 0.000, Max Loss: 0.000, Average: 0.000" />


	public void dump_summary(boolean CruisControl, String Name) {
		SummaryElement base, compare;
		int i=0;
		base = elements.get(0);
		if(!CruisControl) System.out.printf( "%22.22s  |", "");
		if (CruisControl) System.out.printf("<property name=\"%s_%s_cycles\" value=\"",base.get_name(),Name);
		ListIterator<SummaryElement> iter_elem = elements.listIterator(1);
		while(iter_elem.hasNext()) {
			compare = iter_elem.next();
			if(base == compare) continue;
			i++;
			if (CruisControl) 	break;
			if (i==4) break;
			if(!CruisControl) System.out.printf("%22.22s  |",compare.get_name());
		}
		i=0;
		if(!CruisControl) {
			System.out.printf(	"\n------------------------|------------------------|------------------------|------------------------|\n");
			System.out.printf( "%16.16s Cycles |", base.get_name());
		}
		iter_elem = elements.listIterator(1);
		while(iter_elem.hasNext()) {
			compare = iter_elem.next();
			if(base == compare ) continue;
			i++;
			if (i==4 || (i==2 && CruisControl)) break;
			if (compare.get_cycles(CommonData.Dump_Type.Run_Valid).isEmpty()) {
				if(CruisControl) 	System.out.printf(" Max Gain: N/A, Max loss:  N/A, Geomean: N/A");
				else 				System.out.printf(" %.5s / %.5s / %.5s  |","  -  ","  -  ","  -  ");
			}
			else {
				Integer nb_data=0;
				double sumb=0,sumc=0,min=0,max=0,tmp_val;
				ListIterator<Long> vb = base.get_cycles(CommonData.Dump_Type.Run_Valid).listIterator();
				ListIterator<Long> vc = compare.get_cycles(CommonData.Dump_Type.Run_Valid).listIterator();
				while (vb.hasNext() && vc.hasNext()) {
					Long valb = vb.next();
					Long valc = vc.next();
					if (valb <= -1 || valc <= -1)  continue;
					nb_data++;
					sumb += valb; sumc+=valc;
					tmp_val= (valb.doubleValue() - valc.doubleValue())/ valc.doubleValue();
					if (tmp_val > max) max = tmp_val;
					if (tmp_val < min) min = tmp_val;
				}
				double moy = (((sumb / nb_data.doubleValue())-(sumc/nb_data.doubleValue()))/(sumc/nb_data.doubleValue()));
				if(CruisControl) System.out.printf(" Max Gain: %5.5s, Max Loss: %5.5s, Geomean: %5.5s",
						String.format("%4.4f",min*100.00), 
						String.format("%4.4f",max*100.00), 
						String.format("%4.4f",moy*100.00));					
				else 	System.out.printf(" %5.5s / %5.5s / %5.5s  |",
						String.format("%4.4f",min*100.00),
						String.format("%4.4f",max*100.00),
						String.format("%4.4f",moy*100.00));	
			}
		}
		if (CruisControl) System.out.printf("\" />\n<property name=\"%s_%s_func_size\" value=\"",base.get_name(),Name);
		else System.out.printf( "\n%14.14s Size Func |", base.get_name());
		i=0;
		iter_elem = elements.listIterator(1);
		while(iter_elem.hasNext()) {
			compare = iter_elem.next();
			if(base == compare ) continue;
			i++;
			if (i==4 || (i==2 && CruisControl)) break;
			if (compare.get_size(CommonData.Dump_Type.Run_Valid,Discriminent.SIZE_FUNC).isEmpty()) {
				if(CruisControl) 	System.out.printf(" Max Gain: N/A, Max loss:  N/A, Geomean: N/A");
				else 				System.out.printf(" %.5s / %.5s / %.5s  |","  -  ","  -  ","  -  ");
			}
			else {
				Integer nb_data=0;
				double sumb=0,sumc=0,min=0,max=0,tmp_val;
				ListIterator<Long> vb = base.get_size(CommonData.Dump_Type.Run_Valid,Discriminent.SIZE_FUNC).listIterator();
				ListIterator<Long> vc = compare.get_size(CommonData.Dump_Type.Run_Valid,Discriminent.SIZE_FUNC).listIterator();
				while (vb.hasNext() && vc.hasNext()) {
					Long valb = vb.next();
					Long valc = vc.next();
					if (valb <= -1 || valc <= -1)  continue;
					nb_data++;
					sumb += valb; sumc+=valc;
					tmp_val= (valb.doubleValue() - valc.doubleValue())/ valc.doubleValue();
					if (tmp_val > max) max = tmp_val;
					if (tmp_val < min) min = tmp_val;
				}
				double moy = (((sumb / nb_data.doubleValue())-(sumc/nb_data.doubleValue()))/(sumc/nb_data.doubleValue()));
				if(CruisControl) System.out.printf(" Max Gain: %5.5s, Max Loss: %5.5s, Geomean: %5.5s",
						String.format("%4.4f",min*100.00), 
						String.format("%4.4f",max*100.00), 
						String.format("%4.4f",moy*100.00));					
				else 	System.out.printf(" %5.5s / %5.5s / %5.5s  |",
						String.format("%4.4f",min*100.00),
						String.format("%4.4f",max*100.00),
						String.format("%4.4f",moy*100.00));	
			}
		}
		i=0;
		if (CruisControl) System.out.printf("\" />\n<property name=\"%s_%s_bin_size\" value=\"",base.get_name(),Name);
		else System.out.printf( "\n%14.14s Size Bin |", base.get_name());
		iter_elem = elements.listIterator(1);
		while(iter_elem.hasNext()) {
			compare = iter_elem.next();
			if(base == compare ) continue;
			i++;
			if (i==4 || (i==2 && CruisControl)) break;
			if (compare.get_size(CommonData.Dump_Type.Run_Valid,Discriminent.SIZE_BIN).isEmpty()) {
				if(CruisControl) 	System.out.printf(" Max Gain: N/A, Max loss:  N/A, Geomean: N/A");
				else 				System.out.printf(" %.5s / %.5s / %.5s  |","  -  ","  -  ","  -  ");
			}
			else {
				Integer nb_data=0;;
				double sumb=0,sumc=0,min=0,max=0,tmp_val;
				ListIterator<Long> vb = base.get_size(CommonData.Dump_Type.Run_Valid,Discriminent.SIZE_BIN).listIterator();
				ListIterator<Long> vc = compare.get_size(CommonData.Dump_Type.Run_Valid,Discriminent.SIZE_BIN).listIterator();
				while (vb.hasNext() && vc.hasNext()) {
					Long valb = vb.next();
					Long valc = vc.next();
					if (valb <= -1 || valc <= -1)  continue;
					nb_data++;
					sumb += valb; sumc+=valc;
					tmp_val= (valb.doubleValue() - valc.doubleValue())/ valc.doubleValue();
					if (tmp_val > max) max = tmp_val;
					if (tmp_val < min) min = tmp_val;
				}
				double moy = (((sumb / nb_data.doubleValue())-(sumc/nb_data.doubleValue()))/(sumc/nb_data.doubleValue()));
				if(CruisControl) System.out.printf(" Max Gain: %5.5s, Max Loss: %5.5s, Geomean: %5.5s",
						String.format("%4.4f",min*100.00), 
						String.format("%4.4f",max*100.00), 
						String.format("%4.4f",moy*100.00));					
				else 	System.out.printf(" %5.5s / %5.5s / %5.5s  |",
						String.format("%4.4f",min*100.00),
						String.format("%4.4f",max*100.00),
						String.format("%4.4f",moy*100.00));	
			}
		}
		if (CruisControl) System.out.printf("\" />\n");
		else System.out.printf( "\n");
	}
	
	public ArrayList<SummaryElement> get_list_elem() {return elements;};
}
