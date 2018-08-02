package org.opencb.biodata.tools.feature;

import org.broad.igv.bbfile.*;
import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.tools.commons.ChunkFrequencyManager;
import org.opencb.commons.utils.FileUtils;
import org.opencb.commons.utils.ListUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by imedina on 25/11/16.
 */
public class BigWigManager {

    private Path bigWigFilePath;
    private BBFileReader bbFileReader;
    private List<Integer> zoomWindowSizes;

    @Deprecated
    private Path indexPath;

    @Deprecated
    public static final String BIGWIG_DB = "bigwig.db";

    /**
     * Constructor.
     *
     * @param bigwigPath  Path to the Big Wig file
     * @throws IOException
     */
    public BigWigManager(Path bigwigPath) throws IOException {
        this.bigWigFilePath = bigwigPath;

        init();
    }

    @Deprecated
    public BigWigManager(Path bigwigPath, Path indexPath) throws IOException {
        this.bigWigFilePath = bigwigPath;
        this.indexPath = indexPath;

        init();
    }

    private void init() throws IOException {
        FileUtils.checkPath(this.bigWigFilePath);

        bbFileReader = new BBFileReader(this.bigWigFilePath.toString());
        zoomWindowSizes = new ArrayList<>();
        for (int zoomLevel = 1; zoomLevel <= bbFileReader.getZoomLevelCount(); zoomLevel++) {
            zoomWindowSizes.add(bbFileReader.getZoomLevels().getZoomLevelHeader(zoomLevel).getReductionLevel());
        }
    }

    /**
     * Query by a given region.
     *
     * @param region    Region target
     * @return          Array of floating values for that region
     * @throws IOException
     */
    public float[] query(Region region) throws IOException {
        BigWigIterator bigWigIterator = iterator(region);
        float[] values = new float[region.getEnd() - region.getStart() + 1];
        while (bigWigIterator.hasNext()) {
            WigItem wigItem = bigWigIterator.next();
            for (int i = wigItem.getStartBase(), j = wigItem.getStartBase() - region.getStart(); i <= wigItem.getEndBase(); i++, j++) {
                values[j] = wigItem.getWigValue();
            }
        }
        return values;
    }

    /**
     * Get the iterator for the given region.
     *
     * @param region    Region target
     * @return          Big Wig file iterator
     */
    public BigWigIterator iterator(Region region) {
        return bbFileReader.getBigWigIterator(region.getChromosome(), region.getStart(), region.getChromosome(), region.getEnd(), false);
    }

    public ZoomLevelIterator iterator(Region region, int zoomLevel) {
        return bbFileReader.getZoomLevelIterator(zoomLevel, region.getChromosome(), region.getStart(), region.getChromosome(), region.getEnd(), false);
    }

    public float[] groupBy(Region region, int windowSize) {
        int zoomLevel = -1;
        for (int level = 0; level < zoomWindowSizes.size(); level++) {
            if (windowSize < zoomWindowSizes.get(level)) {
                break;
            }
            zoomLevel++;
        }

        // Calculate the number of needed windows, ensure windowSize => 1
        windowSize = Math.max(1, windowSize);
        int numWindows = (region.getEnd() - region.getStart()) / windowSize;
        if ((region.getEnd() - region.getStart()) % windowSize != 0) {
            numWindows++;
        }
        float[] chunks = new float[numWindows];

        if (zoomLevel == -1) {
            // No zoom level available. This can happen because there are not zoom levels or the window size is too small
            BigWigIterator bigWigIterator = iterator(region);
            WigItem wItem;
            int length, chunkStart, chunkEnd;
            while (bigWigIterator.hasNext()) {
                wItem = bigWigIterator.next();
                chunkStart = (Math.max(region.getStart(), wItem.getStartBase()) - region.getStart()) / windowSize;
                chunkEnd = (Math.min(region.getEnd(), wItem.getEndBase()) - region.getStart() - 1) / windowSize;
                for (int chunk = chunkStart; chunk <= chunkEnd; chunk++) {
                    length = Math.min(wItem.getEndBase() - region.getStart(), chunk * windowSize + windowSize)
                            - Math.max(wItem.getStartBase() - region.getStart(), chunk * windowSize);
                    chunks[chunk] += (wItem.getWigValue() * length);
                }
            }
        } else {
            // We get the zoom iterator, we need to increment by 1.
            ZoomLevelIterator zoomIterator = iterator(region, zoomLevel + 1);
            ZoomDataRecord wItem;
            int length, chunkStart, chunkEnd;
            while (zoomIterator.hasNext()) {
                wItem = zoomIterator.next();
                chunkStart = (Math.max(region.getStart(), wItem.getChromStart()) - region.getStart()) / windowSize;
                chunkEnd = (Math.min(region.getEnd(), wItem.getChromEnd()) - region.getStart() - 1) / windowSize;
                for (int chunk = chunkStart; chunk <= chunkEnd; chunk++) {
                    length = Math.min(wItem.getChromEnd() - region.getStart(), chunk * windowSize + windowSize)
                            - Math.max(wItem.getChromStart() - region.getStart(), chunk * windowSize);
                    chunks[chunk] += (wItem.getMeanVal() * length);
                }
            }
        }

        for (int i = 0; i < chunks.length; i++) {
            chunks[i] /= windowSize;
        }

        return chunks;
    }

    /**
     * Index the entire Big Wig file content in a SQLite database.
     *
     * @return              Path to the index (database file)
     * @throws Exception
     */
    public Path index() throws Exception {
        return index(bigWigFilePath);
    }

    /**
     * Index the entire Big Wig file content in a SQLite database.
     *
     * @param bigwigPath    Path to the Big Wig file to index
     * @return              Path to the index (database file)
     * @throws Exception
     */
    @Deprecated
    public Path index(Path bigwigPath) throws Exception {
//        FileUtils.checkFile(indexPath);
        ChunkFrequencyManager chunkFrequencyManager = new ChunkFrequencyManager(indexPath);

        // get the chunk size
        int chunkSize = chunkFrequencyManager.getChunkSize();

        int prevChunk = 0, startChunk, endChunk, partial;
        String currChrom, prevChrom = null;
        List<Integer> values = new ArrayList<>();

        // and then iterate BigWig file
        BigWigIterator bwIterator = bbFileReader.getBigWigIterator();
        while (bwIterator.hasNext()) {
            WigItem wigItem = bwIterator.next();

            // get info from wig item
            currChrom = wigItem.getChromosome();
            startChunk = wigItem.getStartBase() / chunkSize;
            endChunk = wigItem.getEndBase() / chunkSize;

            // chromosome change, we must store previous chromosome values
            if (prevChrom != currChrom) {
                if (values.size() > 0) {
                    WigUtils.computeAndSaveMeanValues(values, bigwigPath, prevChrom, chunkSize, chunkFrequencyManager);
                }
                currChrom = prevChrom;
            }

            if (prevChunk != startChunk) {
                for (int chunk = prevChunk; chunk < startChunk; chunk++) {
                    values.add(0);
                }
            }

            for (int chunk = startChunk, pos = startChunk * chunkSize;
                 chunk <= endChunk;
                 chunk++, pos += chunkSize) {
                // compute how many values are within the current chunk
                // and update the chunk
                partial = Math.min(wigItem.getEndBase(), pos + chunkSize) - Math.max(wigItem.getStartBase(), pos);
                values.add((int) (partial * wigItem.getWigValue()));
            }
            prevChunk = endChunk;
        }

        if (values.size() > 0) {
            WigUtils.computeAndSaveMeanValues(values, bigwigPath, prevChrom, chunkSize, chunkFrequencyManager);
        }

        return indexPath;
    }

    public List<Integer> getZoomWindowSizes() {
        return zoomWindowSizes;
    }

}

