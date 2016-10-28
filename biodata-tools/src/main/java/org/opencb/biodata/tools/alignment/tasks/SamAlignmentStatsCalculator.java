package org.opencb.biodata.tools.alignment.tasks;

import htsjdk.samtools.Cigar;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.SAMRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pfurio on 28/10/16.
 */
public class SamAlignmentStatsCalculator extends AlignmentStatsCalculator<SAMRecord> {

    @Override
    public boolean isProperlyPaired(SAMRecord alignment) {
        return alignment.getReadPairedFlag() && alignment.getProperPairFlag();
    }

    @Override
    public int getInsertSize(SAMRecord alignment) {
        return alignment.getInferredInsertSize();
    }

    @Override
    public boolean isFirstOfPair(SAMRecord alignment) {
        return alignment.getFirstOfPairFlag();
    }

    @Override
    public boolean isSecondOfPair(SAMRecord alignment) {
        return alignment.getSecondOfPairFlag();
    }

    @Override
    public int getMappingQuality(SAMRecord alignment) {
        return alignment.getMappingQuality();
    }

    @Override
    public String getAlignedSequence(SAMRecord alignment) {
        return alignment.getReadString();
    }

    @Override
    public List<Integer> getAlignedQuality(SAMRecord alignment) {
        byte[] baseQualities = alignment.getBaseQualities();
        List<Integer> qualityList = new ArrayList<>(baseQualities.length);

        for (byte qual : baseQualities){
            qualityList.add((int) qual);
        }
        return qualityList;
//        return parseQualityString(alignment.getBaseQualityString());
    }

    @Override
    public CIGAR getActiveCigars(SAMRecord alignment) {
        CIGAR ret = new CIGAR();

        Cigar cigar = alignment.getCigar();
        if (cigar != null) {
            for (CigarElement cigarElement : cigar.getCigarElements()) {
                switch (cigarElement.getOperator()) {
                    case I:
                        ret.in = true;
                        break;
                    case D:
                        ret.del = true;
                        break;
                    case N:
                        ret.skip = true;
                        break;
                    case S:
                        ret.soft = true;
                        break;
                    case H:
                        ret.hard = true;
                        break;
                    case P:
                        ret.pad = true;
                        break;
                    default:
                        break;
                }
            }
        }

        return ret;
    }

    @Override
    public int getNumberOfMismatches(SAMRecord alignment) {
        List<SAMRecord.SAMTagAndValue> attributes = alignment.getAttributes();
        for (SAMRecord.SAMTagAndValue attribute : attributes) {
            if (attribute.tag.equals("NM")) {
                return (int) attribute.value;
            }
        }
        return 0;
    }

    @Override
    public boolean isMapped(SAMRecord alignment) {
        return !alignment.getReadUnmappedFlag();
    }

}
