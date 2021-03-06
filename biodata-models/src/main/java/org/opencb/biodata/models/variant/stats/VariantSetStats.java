/*
 * <!--
 *   ~ Copyright 2015-2017 OpenCB
 *   ~
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at
 *   ~
 *   ~     http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License.
 *   -->
 *
 */

package org.opencb.biodata.models.variant.stats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.opencb.biodata.models.variant.avro.VariantType;
import org.opencb.biodata.models.variant.metadata.ChromosomeStats;
import org.opencb.biodata.models.variant.metadata.VariantsByFrequency;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
@JsonIgnoreProperties({"impl",
        "variantsCount",
        "numRecords",
        "samplesCount",
        "passCount",
        "chromosomeCounts",
        "consequenceTypesCount",
        "snpsCount",
        "indelsCount",
        "structuralCount"})
public class VariantSetStats {

    private final org.opencb.biodata.models.variant.metadata.VariantSetStats impl;

    public VariantSetStats(org.opencb.biodata.models.variant.metadata.VariantSetStats impl) {
        this.impl = impl;
    }

    public VariantSetStats() {
        this.impl = new org.opencb.biodata.models.variant.metadata.VariantSetStats();
        this.impl.setVariantTypeCounts(new HashMap<>());
        this.impl.setChromosomeStats(new HashMap<>());
        this.impl.setNumRareVariants(new ArrayList<>());
        this.impl.setVariantBiotypeCounts(new HashMap<>());
        this.setNumVariants(0);
        this.setNumSamples(0);
        this.setSnpsCount(0);
        this.setIndelsCount(0);
        this.setStructuralCount(0);
        this.setNumPass(0);
        this.setConsequenceTypesCounts(new LinkedHashMap<>(20));
    }

    public VariantSetStats(int numRecords, int samplesCount, int snpsCount, int indelsCount, int structuralCount,
                           int passCount, float meanQuality, Map<String, Integer> consequenceTypesCount) {
        this.impl = new org.opencb.biodata.models.variant.metadata.VariantSetStats();
        this.impl.setVariantTypeCounts(new HashMap<>());
        this.impl.setChromosomeStats(new HashMap<>());
        this.setNumVariants(numRecords);
        this.setNumSamples(samplesCount);
        this.setSnpsCount(snpsCount);
        this.setIndelsCount(indelsCount);
        this.setStructuralCount(structuralCount);
        this.setNumPass(passCount);
        this.setMeanQuality(meanQuality);
        this.setConsequenceTypesCounts(consequenceTypesCount);
    }

    public org.opencb.biodata.models.variant.metadata.VariantSetStats getImpl() {
        return impl;
    }

    @Deprecated
    public int getVariantsCount() {
        return getNumVariants();
    }

    @Deprecated
    public void setVariantsCount(int variantsCount) {
        this.setNumVariants(variantsCount);
    }

    public int getNumVariants() {
        return impl.getNumVariants() == null ? 0 : impl.getNumVariants();
    }

    public void setNumVariants(Integer numRecords) {
        this.impl.setNumVariants(numRecords);
    }

    @Deprecated
    public int getNumRecords() {
        return getNumVariants();
    }

    @Deprecated
    public void setNumRecords(Integer numRecords) {
        this.setNumVariants(numRecords);
    }

    public int getNumSamples() {
        return impl.getNumSamples();
    }

    public void setNumSamples(int samplesCount) {
        this.impl.setNumSamples(samplesCount);
    }

    @Deprecated
    public int getSamplesCount() {
        return getNumSamples();
    }

    @Deprecated
    public void setSamplesCount(int samplesCount) {
        this.setNumSamples(samplesCount);
    }

    public int getNumPass() {
        return impl.getNumPass();
    }

    public void setNumPass(int passCount) {
        this.impl.setNumPass(passCount);
    }

    public void addNumPass(int passCount) {
        this.impl.setNumPass(impl.getNumPass() + passCount);
    }

    @Deprecated
    public int getPassCount() {
        return getNumPass();
    }

    @Deprecated
    public void setPassCount(int passCount) {
        this.setNumPass(passCount);
    }

    public int getSnpsCount() {
        return getVariantTypeCount(VariantType.SNP);
    }

    public void setSnpsCount(int snpsCount) {
        setVariantTypeCount(VariantType.SNP, snpsCount);
    }

    public int getIndelsCount() {
        return getVariantTypeCount(VariantType.INDEL);
    }

    public void setIndelsCount(int indelsCount) {
        setVariantTypeCount(VariantType.INDEL, indelsCount);
    }

    public int getStructuralCount() {
        return getVariantTypeCount(VariantType.SV);
    }

    public void setStructuralCount(int structuralCount) {
        setVariantTypeCount(VariantType.SV, structuralCount);
    }

    public Map<String, Integer> getVariantTypeCounts() {
        return impl.getVariantTypeCounts();
    }

    public void setVariantTypeCounts(Map<String, Integer> count) {
        impl.setVariantTypeCounts(count);
    }

    public int getVariantTypeCount(VariantType key) {
        return impl.getVariantTypeCounts().getOrDefault(key.toString(), 0);
    }

    public void setVariantTypeCount(VariantType key, int count) {
        impl.getVariantTypeCounts().put(key.toString(), count);
    }

    public void addVariantTypeCount(VariantType key, int count) {
        impl.getVariantTypeCounts().put(key.toString(), getVariantTypeCount(key) + count);
    }

    public Map<String, ChromosomeStats> getChromosomeStats() {
        return impl.getChromosomeStats();
    }

    public void setChromosomeStats(Map<String, ChromosomeStats> chromosomeStats) {
        impl.setChromosomeStats(chromosomeStats);
    }

    public Map<String, Integer> getChromosomeCounts() {
        return impl.getChromosomeStats().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, o -> o.getValue().getCount()));
    }

    public void setChromosomeCounts(Map<String, Integer> counts) {
        counts.forEach(this::setChromosomeCount);
    }

    private int getChromosomeCount(String chromosome) {
        ChromosomeStats chromosomeStats = impl.getChromosomeStats().get(chromosome);
        return chromosomeStats == null ? 0 : chromosomeStats.getCount();
    }

    public void setChromosomeCount(String chromosome, int count) {
        impl.getChromosomeStats().compute(chromosome, (key, chromosomeStats) ->  {
            if (chromosomeStats == null) {
                return new ChromosomeStats(count, 0F);
            } else {
                chromosomeStats.setCount(count);
                return chromosomeStats;
            }
        });
    }

    public void addChromosomeCount(String chromosome, int count) {
        impl.getChromosomeStats().compute(chromosome, (key, chromosomeStats) -> {
            if (chromosomeStats == null) {
                return new ChromosomeStats(count, 0F);
            } else {
                chromosomeStats.setCount(chromosomeStats.getCount() + count);
                return chromosomeStats;
            }
        });
    }

    public float getTiTvRatio() {
        return this.impl.getTiTvRatio();
    }

    public void setTiTvRatio(float tiTvRatio) {
        this.impl.setTiTvRatio(tiTvRatio);
    }

    public void setTiTvRatio(int transitionsCount, int transversionsCount) {
        setTiTvRatio(((float) transitionsCount) / transversionsCount);
    }

    public Float getMeanQuality() {
        return impl.getMeanQuality();
    }

    public void setMeanQuality(Float meanQuality) {
        this.impl.setMeanQuality(meanQuality);
    }

    public float getStdDevQuality(){
        return impl.getStdDevQuality();
    }

    public void setStdDevQuality(float value) {
        this.impl.setStdDevQuality(value);
    }

    public List<VariantsByFrequency> getNumRareVariants() {
        return impl.getNumRareVariants();
    }

    public void setNumRareVariants(List<VariantsByFrequency> numRareVariants) {
        impl.setNumRareVariants(numRareVariants);
    }

    public Map<String, Integer> getConsequenceTypesCounts() {
        return impl.getConsequenceTypesCounts();
    }

    public void setConsequenceTypesCounts(Map<String, Integer> consequenceTypesCount) {
        this.impl.setConsequenceTypesCounts(consequenceTypesCount);
    }

    public void addConsequenceTypeCounts(String ct, int count) {
        getConsequenceTypesCounts().compute(ct, (k, prevCount) -> prevCount == null ? count : prevCount + count);
    }

    public Map<String, Integer> getVariantBiotypeCounts() {
        return impl.getVariantBiotypeCounts();
    }

    public void setVariantBiotypeCounts(Map<String, Integer> variantBiotypeCounts) {
        this.impl.setVariantBiotypeCounts(variantBiotypeCounts);
    }

    public void addVariantBiotypeCounts(String biotype, int count) {
        getVariantBiotypeCounts().compute(biotype, (k, prevCount) -> prevCount == null ? count : prevCount + count);
    }

    @Deprecated
    public Map<String, Integer> getConsequenceTypesCount() {
        return getConsequenceTypesCounts();
    }

    @Deprecated
    public void setConsequenceTypesCount(Map<String, Integer> consequenceTypesCount) {
        this.setConsequenceTypesCounts(consequenceTypesCount);
    }

    @Deprecated
    public void update(VariantStats stats) {
//        this.setNumVariants(getNumVariants() + 1);
//
//        addVariantTypeCount(stats.getVariantType(), 1);
//
//        if (stats.hasPassedFilters()) {
//            this.setNumPass(getNumPass() + 1);
//        }
//
//        this.setNumSamples(stats.getNumSamples());
//        setTransitionsCount(getTransitionsCount() + (stats.isTransition() ? 1 : 0));
//        setTransversionsCount(getTransversionsCount() + (stats.isTransversion() ? 1 : 0));
//        setAccumulatedQuality(getAccumulatedQuality() + stats.getQuality());
    }

    @Override
    public String toString() {
        return impl.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VariantSetStats)) return false;

        VariantSetStats that = (VariantSetStats) o;

        return !(impl != null ? !impl.equals(that.impl) : that.impl != null);
    }

    @Override
    public int hashCode() {
        return impl.hashCode();
    }

}
