package org.opencb.biodata.tools.alignment.iterators;

import ga4gh.Reads;
import htsjdk.samtools.SAMRecordIterator;
import org.opencb.biodata.tools.alignment.converters.SAMRecordToProtoReadAlignmentConverter;
import org.opencb.biodata.tools.alignment.filtering.AlignmentFilters;

/**
 * Created by pfurio on 25/10/16.
 */
public class ProtoIterator extends AlignmentIterator<Reads.ReadAlignment> {

    private SAMRecordToProtoReadAlignmentConverter protoReadAlignmentConverter;

    public ProtoIterator(SAMRecordIterator samRecordIterator) {
        this(samRecordIterator, null, true);
    }

    public ProtoIterator(SAMRecordIterator samRecordIterator, AlignmentFilters filters) {
        this(samRecordIterator, filters, true);
        protoReadAlignmentConverter = new SAMRecordToProtoReadAlignmentConverter();
    }

    public ProtoIterator(SAMRecordIterator samRecordIterator, AlignmentFilters filters, boolean binQualities) {
        super(samRecordIterator, filters);
        protoReadAlignmentConverter = new SAMRecordToProtoReadAlignmentConverter(binQualities);
    }

    @Override
    public boolean hasNext() {
        return prevNext != null;
    }

    @Override
    public Reads.ReadAlignment next() {
        Reads.ReadAlignment readAlignment = protoReadAlignmentConverter.to(prevNext);
        moveIterator();
        return readAlignment;
    }

}
