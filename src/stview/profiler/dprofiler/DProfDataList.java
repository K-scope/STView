package stview.profiler.dprofiler;

import java.util.ArrayList;

/**
 * Profiler data list for Dprof class.
 */
public class DProfDataList {
	protected int procNum;
	protected int procNo;
	protected int meaOpt;
	protected short threadNum;
	private ArrayList<String> filelist = new ArrayList<String>();
	private ArrayList<SymbolInfo> symbollist = new ArrayList<SymbolInfo>();
	private ArrayList<SymbolInfo> totalSymbollist = new ArrayList<SymbolInfo>();
	private ArrayList<LineInfo> linelist = new ArrayList<LineInfo>();
	private ArrayList<LineInfo> totalLinelist = new ArrayList<LineInfo>();
	private ArrayList<LoopInfo> looplist = new ArrayList<LoopInfo>();
	private ArrayList<LoopInfo> totalLooplist = new ArrayList<LoopInfo>();
	
	/**
	 * Getter for the member of the filelist.
	 * @param i Index
	 * @return The file name
	 */
	public String getFile(int i){
		if(filelist.size() < i) return "";
		return filelist.get(i);
	}
	
	/**
	 * Getter for the member of the symbollist.
	 * @param i Index
	 * @return The symbol name
	 */
	public String getSymbol(int i){
		if(symbollist.size() < i) return "";
		return symbollist.get(i).symbolName;
	}
	
	/**
	 * Getter for the symbollist.
	 * @param i A thread number
	 * @return The symbollist
	 */
	public ArrayList<String> getSymbolList(int threadNo){
		ArrayList<String> list = new ArrayList<String>();
		if(threadNo < 0){
			for(int i=0; i<totalSymbollist.size(); i++)
				list.add(totalSymbollist.get(i).getString());
			return list;	
		}
		for(int i=0; i<symbollist.size(); i++)
            if(symbollist.get(i).threadNo == threadNo)
                list.add(symbollist.get(i).getString());
		return list;
	}
	
	/**
	 * Getter for the linelist.
	 * @param i A thread number
	 * @return The linelist
	 */
	public ArrayList<String> getLineList(int threadNo){
		ArrayList<String> list = new ArrayList<String>();
		if(threadNo < 0){
			for(int i=0; i<totalLinelist.size(); i++)
				list.add(totalLinelist.get(i).getString());
			return list;	
		}
		for(int i=0; i<linelist.size(); i++)
            if(linelist.get(i).threadNo == threadNo)
                list.add(linelist.get(i).getString());
		return list;
	}
	
	/**
	 * Getter for the looplist.
	 * @param i A thread number
	 * @return The looplist
	 */
	public ArrayList<String> getLoopList(int threadNo){
		ArrayList<String> list = new ArrayList<String>();
		if(threadNo < 0){
			for(int i=0; i<totalLooplist.size(); i++)
				list.add(totalLooplist.get(i).getString());
			return list;	
		}
		for(int i=0; i<looplist.size(); i++)
            if(looplist.get(i).threadNo == threadNo)
                list.add(looplist.get(i).getString());
		return list;
	}
	
	/**
	 * Add A file name.
	 * @param tmp A file name
	 */
	protected void addFile(String tmp){
		filelist.add(tmp);
	}
	
	/**
	 * Add A symbol name.
	 * @param tmp A symbol name
	 */
	protected void addSymbol(String tmp){
		String[] list = tmp.split(",");
		if(list.length == 8)
			symbollist.add(new SymbolInfo(list));
	}
    
	/**
	 * Create the total symbol list
	 */
    protected void createTotalSymbolList(){
        if(symbollist == null || symbollist.isEmpty()) return;

        for(int i=0; i<symbollist.size(); i++){
        	boolean f = true;
            for(int j=0; j<totalSymbollist.size(); j++){
            	if(symbollist.get(i).symbolName.equals(totalSymbollist.get(j).symbolName)){
            		if(symbollist.get(i).fileIndex==totalSymbollist.get(j).fileIndex){
            			f = false;
            			totalSymbollist.get(j).sampNum+=symbollist.get(i).sampNum;
            			totalSymbollist.get(j).syncNum+=symbollist.get(i).syncNum;
            			totalSymbollist.get(j).mpiCost+=symbollist.get(i).mpiCost;
            		}
            	}
            	if(!f) break;	
            }
            if(f){
            	totalSymbollist.add(new SymbolInfo(symbollist.get(i).getString().split(",")));
            }
        }
        for(int i=0; i<totalSymbollist.size(); i++)
        	totalSymbollist.get(i).threadNo = -1;
    }
	
	/**
	 * Add A line name.
	 * @param tmp A line name
	 */
    protected void addLine(String tmp){
		String[] list = tmp.split(",");
		if(list.length == 7)
			linelist.add(new LineInfo(list));
	}
    
	/**
	 * Create the total line list
	 */
    protected void createTotalLineList(){
        if(linelist == null || linelist.isEmpty()) return;

        for(int i=0; i<linelist.size(); i++){
        	boolean f = true;
            for(int j=0; j<totalLinelist.size(); j++){
            	if(linelist.get(i).fileIndex==totalLinelist.get(j).fileIndex){
            		if(linelist.get(i).symbolIndex==totalLinelist.get(j).symbolIndex){
            			if(linelist.get(i).line==totalLinelist.get(j).line){
            				f = false;
            				totalLinelist.get(j).sampNum+=linelist.get(i).sampNum;
            				totalLinelist.get(j).syncNum+=linelist.get(i).syncNum;
            				totalLinelist.get(j).mpiCost+=linelist.get(i).mpiCost;
            			}
            		}
            	}
            	if(!f) break;	
            }
            if(f){
            	totalLinelist.add(new LineInfo(linelist.get(i).getString().split(",")));
            }
        }
        for(int i=0; i<totalLinelist.size(); i++)
        	totalLinelist.get(i).threadNo = -1;
    }
    
	/**
	 * Add A loop name.
	 * @param tmp A loop name
	 */
    protected void addLoop(String tmp){
		String[] list = tmp.split(",");
		if(list.length == 11)
			looplist.add(new LoopInfo(list));
	}
    
	/**
	 * Create the total loop list
	 */
    protected void createTotalLoopList(){
        if(looplist == null || looplist.isEmpty()) return;

        for(int i=0; i<looplist.size(); i++){
        	boolean f = true;
            for(int j=0; j<totalLooplist.size(); j++){
            	if(looplist.get(i).fileIndex==totalLooplist.get(j).fileIndex){
            		if(looplist.get(i).symbolIndex==totalLooplist.get(j).symbolIndex){
            			if(looplist.get(i).lineStart==totalLooplist.get(j).lineStart){
            				if(looplist.get(i).lineEnd==totalLooplist.get(j).lineEnd){
            					f = false;
            					totalLooplist.get(j).sampNum+=looplist.get(i).sampNum;
            					totalLooplist.get(j).syncNum+=looplist.get(i).syncNum;
            					totalLooplist.get(j).mpiCost+=looplist.get(i).mpiCost;
            				}
            			}
            		}
            	}
            	if(!f) break;	
            }
            if(f){
            	totalLooplist.add(new LoopInfo(looplist.get(i).getString().split(",")));
            }
        }
        for(int i=0; i<totalLooplist.size(); i++)
        	totalLooplist.get(i).threadNo = -1;
    }
	
    /**   Symbol information class.  */
	public class SymbolInfo {
        short threadNo;
		float sampNum;
		float syncNum;
		float mpiCost;
		int lineStart;
		int lineEnd;
		int fileIndex;
		String symbolName;
		
		public SymbolInfo(String[] list){
            threadNo = Short.valueOf(list[0]);
			sampNum = Float.valueOf(list[1]);
			syncNum = Float.valueOf(list[2]);
			mpiCost = Float.valueOf(list[3]);
			lineStart = Integer.valueOf(list[4]);
			lineEnd = Integer.valueOf(list[5]);
			fileIndex = Integer.valueOf(list[6]);
			symbolName = list[7];
		}
		
		public String getString(){
			String tmp = threadNo+",";
            tmp += sampNum+",";
			tmp += syncNum+",";
			tmp += mpiCost+",";
			tmp += lineStart+",";
			tmp += lineEnd+",";
			tmp += fileIndex+",";
			tmp += symbolName;
			return tmp;
		}
	}
	
	/**   Line information class.  */
	public class LineInfo {
		short threadNo;
		float sampNum;
		float syncNum;
		float mpiCost;
		int line;
		int symbolIndex;
		int fileIndex;
		
		public LineInfo(String[] list){
			threadNo = Short.valueOf(list[0]);
			sampNum = Float.valueOf(list[1]);
			syncNum = Float.valueOf(list[2]);
			mpiCost = Float.valueOf(list[3]);
			line = Integer.valueOf(list[4]);
			symbolIndex = Integer.valueOf(list[5]);
			fileIndex = Integer.valueOf(list[6]);
		}
		
		public String getString(){
			String tmp = threadNo+",";
			tmp += sampNum+",";
			tmp += syncNum+",";
			tmp += mpiCost+",";
			tmp += line+",";
			tmp += symbolIndex+",";
			tmp += fileIndex;
			return tmp;
		}
	}
	
	/**   Loop information class.  */
	public class LoopInfo {
		short threadNo;
		float sampNum;
		float syncNum;
		float mpiCost;
		int lineStart;
		int lineEnd;
		int nest;
		short type;
		short parallel;
		int symbolIndex;
		int fileIndex;
		
		public LoopInfo(String[] list){
			threadNo = Short.valueOf(list[0]);
			sampNum = Float.valueOf(list[1]);
			syncNum = Float.valueOf(list[2]);
			mpiCost = Float.valueOf(list[3]);
			lineStart = Integer.valueOf(list[4]);
			lineEnd = Integer.valueOf(list[5]);
			nest = Integer.valueOf(list[6]);
			type = Short.valueOf(list[7]);
			parallel = Short.valueOf(list[8]);
			symbolIndex = Integer.valueOf(list[9]);
			fileIndex = Integer.valueOf(list[10]);
		}
		
		public String getString(){
			String tmp = threadNo+",";
			tmp += sampNum+",";
			tmp += syncNum+",";
			tmp += mpiCost+",";
			tmp += lineStart+",";
			tmp += lineEnd+",";
			tmp += nest+",";
			tmp += type+",";
			tmp += parallel+",";
			tmp += symbolIndex+",";
			tmp += fileIndex;
			return tmp;
		}
	}

	/**
	 * Return the kind of the loop.
	 * @param i A number
	 * @return the kind of the loop
	 */
	public static String getType(int i){
		switch(i){
			case 0 :
				return "DO";
			case 3 :
				return "ARRAY";
			default :
				return "UNKNOWN";
		}
	}
	
	/**
	 * Return the kind of the parallel.
	 * @param i A number
	 * @return the kind of the parallel
	 */
	public static String getParallel(int i){
		switch(i){
			case 0 :
				return "SERIAL";
			case 1 :
				return "OpenMP";
			default :
				return "UNKNOWN";
		}
	}
}
