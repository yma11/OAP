package com.intel.oap.common.storage.memkind;

import com.intel.oap.common.storage.stream.PMemManager;
import com.intel.oap.common.storage.stream.ChunkReader;
import com.intel.oap.common.storage.stream.PMemPhysicalAddress;
import com.intel.oap.common.util.MemCopyUtil;

import java.nio.ByteBuffer;

public class MemkindChunkReader extends ChunkReader {
    public MemkindChunkReader(byte[] logicalID, PMemManager pMemManager) {
        super(logicalID, pMemManager);
    }

    @Override
    protected int readFromPMem(PMemPhysicalAddress pMemPhysicalAddress, ByteBuffer data) {
        long baseAddress = ((MemkindPMemPhysicalAddress) pMemPhysicalAddress).getBaseAddress();
        int length = ((MemkindPMemPhysicalAddress) pMemPhysicalAddress).getOffset();
        MemCopyUtil.copyMemory(null, baseAddress, data, data.arrayOffset(), length);
        return length;
    }
}
