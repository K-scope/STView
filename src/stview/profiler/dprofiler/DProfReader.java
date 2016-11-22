package stview.profiler.dprofiler;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Profiler data file reader class.
 */
public class DProfReader {
	public static final int SIZEOF_SHORT = 0x02;
	public static final int SIZEOF_INT = 0x04;
	public static final int SIZEOF_FLOAT = 0x04;
	public static final int SIZEOF_DOUBLE = 0x08;
	public static final int SIZEOF_LONG = 0x08;
	public static final int DPRF_COLL_OPT_PA = 0x00000002;
	public static final int DPRF_COLL_OPT_CALLGRAPH = 0x00000200;
	public static final float VERSION = 1043f;
	private final Map<String, Integer> MAP_PA_INFO_LENGTH = new HashMap<String, Integer>() {
        private static final long serialVersionUID = 1L;
        {
            put("Cache", 10);
            put("Instructions", 9);
            put("MEM_access", 10);
            put("Performance", 10);
            put("Statistics", 10);
        }
    };
	
	int[] threadOrder;
	int lineOffset;
	int loopOffset;
	int callOffset;
	String paEventLength;

	private DProfDataList datalist;
	    
	public DProfDataList load(File file) throws Exception {
		long fileSize = file.length();
		ByteBuffer byteBuf = ByteBuffer.allocate((int) fileSize);
		FileInputStream fis;
		fis = new FileInputStream(file);

		while (fis.available() > 0) {
			byteBuf.put((byte) fis.read());
		}
		byteBuf.flip();

		datalist = new DProfDataList();
		String[] tmp = file.getName().split("_");
		datalist.procNo = Integer.valueOf(tmp[2]);
		if(!readMagicKey(byteBuf)){
			fis.close();
			//message
			return null;
		}
		readCommonInfo(byteBuf);
		readThreadInfo(byteBuf);
		readOffSetInfo(byteBuf);
		readFileInfo(byteBuf);
		readSymbolInfo(byteBuf);
		readLineInfo(byteBuf);
		readLoopInfo(byteBuf);
        //readCallGraphInfo(byteBuf);

		fis.close();		
		return datalist;
	}
	
	public String getString(ByteBuffer byteBuf, int len){
		byte[] tmp = new byte[len];
		byteBuf.get(tmp,0,len);
		return new String(tmp);
	}
	
	public short getShort(ByteBuffer byteBuf){
		byte[] tmp = new byte[SIZEOF_SHORT];
		byteBuf.get(tmp,0,SIZEOF_SHORT);
		return ByteBuffer.wrap(tmp).getShort();      
	}
	
	public int getInt(ByteBuffer byteBuf){
		byte[] tmp = new byte[SIZEOF_INT];
		byteBuf.get(tmp,0,SIZEOF_INT);
		return ByteBuffer.wrap(tmp).getInt();
	}
	
	public float getFloat(ByteBuffer byteBuf){
		byte[] tmp = new byte[SIZEOF_FLOAT];
		byteBuf.get(tmp,0,SIZEOF_FLOAT);
		return ByteBuffer.wrap(tmp).getFloat();
	}
	
	public double getDouble(ByteBuffer byteBuf){
		byte[] tmp = new byte[SIZEOF_DOUBLE];
		byteBuf.get(tmp,0,SIZEOF_DOUBLE);
		return ByteBuffer.wrap(tmp).getDouble();
	}
	
	private boolean readMagicKey(ByteBuffer byteBuf){
		//DPRF
		if(!getString(byteBuf,4).equals("DPRF"))
			return false;
		//Add_mode ?
		getShort(byteBuf);
		//version ?
		if(!(getShort(byteBuf)==VERSION))
			return false;
		return true;
	}
	
	private void readCommonInfo(ByteBuffer byteBuf){
		//process num
		datalist.procNum = getInt(byteBuf);
		//MeasureOption
		datalist.meaOpt = getInt(byteBuf);
		//RunStyle
		getShort(byteBuf);
		//ThreadNum
		datalist.threadNum = getShort(byteBuf);
		//CpuClock
		getInt(byteBuf);
		//MeasureTimeInfo
		getString(byteBuf,32);
		//RecomMemory
		getInt(byteBuf);
        //SampInterval
        getFloat(byteBuf);
        //LogicDimention
        getInt(byteBuf);
        //LogicShape x,y,z
        getInt(byteBuf);
        getInt(byteBuf);
        getInt(byteBuf);
        //LogicCordinate x,y,z
        getInt(byteBuf);
        getInt(byteBuf);
        getInt(byteBuf);
        
        /*//PhisShape x,y,z,a,b,c
        System.out.println("16:"+getInt(byteBuf));
        System.out.println("17:"+getInt(byteBuf));
        System.out.println("18:"+getInt(byteBuf));
        System.out.println("19:"+getInt(byteBuf));
        System.out.println("20:"+getInt(byteBuf));
        System.out.println("21:"+getInt(byteBuf));
        //PhisCordinate x,y,z,a,b,c
        System.out.println("22:"+getInt(byteBuf));
        System.out.println("23:"+getInt(byteBuf));
        System.out.println("24:"+getInt(byteBuf));
        System.out.println("25:"+getInt(byteBuf));
        System.out.println("26:"+getInt(byteBuf));
        System.out.println("27:"+getInt(byteBuf));*/
        
        if((datalist.meaOpt & DPRF_COLL_OPT_PA) != 0){
        	/*PaDiscrimInfo*/
        	//Cpu
        	getShort(byteBuf);
        	//Event_nbr
        	getShort(byteBuf);
        	//Pa_ver
        	getShort(byteBuf);
        	//Reserve
        	getShort(byteBuf);
        	//PaEventVal
        	int i = getInt(byteBuf);
        	paEventLength = getString(byteBuf,i);
        }
	}
	
	private void readThreadInfo(ByteBuffer byteBuf) {
		threadOrder = new int[datalist.threadNum];
		for (int i = 0; i < datalist.threadNum; i++) {
			//ThreadNo
			threadOrder[i] = getInt(byteBuf);
			//ElapsTime
			getFloat(byteBuf);
			//UserTime
			getFloat(byteBuf);
			//SystemTime
			getFloat(byteBuf);
			//TotalSampNum
			getFloat(byteBuf);
			//BarrierWaitSyncNum
			getFloat(byteBuf);
			//MpiLibCostNum
			getFloat(byteBuf);
			//MpiFuncElapsTime
			getFloat(byteBuf);
			
			// ??
			getInt(byteBuf);
			getInt(byteBuf);
			getInt(byteBuf);
			
			if ((datalist.meaOpt & DPRF_COLL_OPT_PA) != 0) {
				int num = MAP_PA_INFO_LENGTH.get(paEventLength);
				double[] paInfo = new double[num];
				for (int j = 0; j < num; j++)
					paInfo[j] = getDouble(byteBuf);
			}
		}
	}
	
	private void readOffSetInfo(ByteBuffer byteBuf){
		//LineInfo
		lineOffset = getInt(byteBuf);
	    //LoopInfo
		loopOffset = getInt(byteBuf);
	    //CallGraphInfo
		callOffset = getInt(byteBuf);
	    //MpiFuncElapsTimeInfo
		getInt(byteBuf);
		//ComInfo
		getInt(byteBuf);
		//SymbolInfo
		getInt(byteBuf);
	}
	
	private void readFileInfo(ByteBuffer byteBuf){
        int fileNameNum = getInt(byteBuf);
        for (int i = 0; i < fileNameNum; i++) {
        	//FileName
            int fileNameLength = getInt(byteBuf);
            datalist.addFile(getString(byteBuf, fileNameLength));
        }
	}
	
	private void readSymbolInfo(ByteBuffer byteBuf){
        for (int i = 0; i < datalist.threadNum; i++) {
            if (byteBuf.position() >= lineOffset) break;
            int symbolNum = getInt(byteBuf);

            for (int j = 0; j < symbolNum; j++) {
                String tmp = threadOrder[i]+",";
            	//SampNum
            	tmp += getFloat(byteBuf)+",";
                //BarrierSyncWaitNum
            	tmp += getFloat(byteBuf)+",";
                //MpiLibCostNum
            	tmp += getFloat(byteBuf)+",";
            	
            	// ??
            	getInt(byteBuf);
            	getInt(byteBuf);
            	getInt(byteBuf);
            	
                //LineSymbolStart
            	tmp += getInt(byteBuf)+",";
                //LineSymbolEnd
            	tmp += getInt(byteBuf)+",";
            	//FileIndex
            	tmp += getInt(byteBuf)+",";
            	//SymbolName
                int symbNameLength = getInt(byteBuf);
                tmp += getString(byteBuf, symbNameLength);
        
                datalist.addSymbol(tmp);
            }
        }
        datalist.createTotalSymbolList();
	}

	private void readLineInfo(ByteBuffer byteBuf){
        if(lineOffset>0){
            byteBuf.position(lineOffset);

            for (int i = 0; i < datalist.threadNum; i++) {
            	if (byteBuf.position() >= loopOffset) break;

                int symbolNum = getInt(byteBuf);
                for (int j = 0; j < symbolNum; j++) {
                	String tmp = threadOrder[i]+",";
                	//SampNum
                	tmp += getFloat(byteBuf)+",";
                    //BarrierSyncWaitNum
                	tmp += getFloat(byteBuf)+",";
                    //MpiLibCostNum
                	tmp += getFloat(byteBuf)+",";
                	
                	// ??
                	getInt(byteBuf);
                	getInt(byteBuf);
                	getInt(byteBuf);
                	
                    //LineNo
                	tmp += getInt(byteBuf)+",";
                    //SymbolIndex
                	tmp += getInt(byteBuf)+",";
                    //FileIndex
                	tmp += getInt(byteBuf);
                	
                	datalist.addLine(tmp);
                }
            }
            datalist.createTotalLineList();
        }
	}
	
	private void readLoopInfo(ByteBuffer byteBuf){
        if(loopOffset>0){
            byteBuf.position(loopOffset);

            for (int i = 0; i < datalist.threadNum; i++) {
                if (callOffset > 0 && byteBuf.position() >= callOffset) break;
                if (byteBuf.position() >=byteBuf.array().length) break;
                
                int symbolNum = getInt(byteBuf);
                for (int j = 0; j < symbolNum; j++) {
                	String tmp = threadOrder[i]+",";
                	//SampNum
                	tmp += getFloat(byteBuf)+",";
                    //BarrierSyncWaitNum
                	tmp += getFloat(byteBuf)+",";
                    //MpiLibCostNum
                	tmp += getFloat(byteBuf)+",";
                	
                	// ??
                	getInt(byteBuf);
                	getInt(byteBuf);
                	getInt(byteBuf);
                	
                    //LineLoopStart
                	tmp += getInt(byteBuf)+",";
                    //LineLoopEnd
                	tmp += getInt(byteBuf)+",";
                    //NestLevel
                	tmp += getInt(byteBuf)+",";
                    //LoopType
                	tmp += getShort(byteBuf)+",";
                    //ParallelInfo
                	tmp += getShort(byteBuf)+",";
                    //SymbolIndex
                	tmp += getInt(byteBuf)+",";
                    //FileIndex
                	tmp += getInt(byteBuf);
                  
                    datalist.addLoop(tmp);
                }
            }
            datalist.createTotalLoopList();
        }
	}
	
	@SuppressWarnings("unused")
	private void readCallGraphInfo(ByteBuffer byteBuf){
        if ((datalist.meaOpt & DPRF_COLL_OPT_CALLGRAPH) != 0 && callOffset > 0) {
            byteBuf.position(callOffset);

            for (int i = 0; i < datalist.threadNum; i++) {
                if (byteBuf.remaining() < SIZEOF_FLOAT) break;
                //TotalSumSampNum
                getFloat(byteBuf);
                //stackNum
                int stackNum = getInt(byteBuf);
                for (int j = 0; j < stackNum; j++) {
                	//NestLevel
                	getInt(byteBuf);
                    //SampNum
                	getFloat(byteBuf);
                    //SumSampNum
                	getFloat(byteBuf);
                    //SymbolName
                    int symbNameLength = getInt(byteBuf);
                    getString(byteBuf, symbNameLength);
                }
            }
        }
	}
}
