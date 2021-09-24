package com.github.jelmerk.knn.hnsw;

import com.github.jelmerk.knn.DistanceFunction;
import com.github.jelmerk.knn.DistanceFunctions;
import com.github.jelmerk.knn.ProgressUpdate;
import com.github.jelmerk.knn.SearchResult;
import com.github.jelmerk.knn.hnsw.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

class KryoHnswIndexTest {

    private HnswIndex<String, byte[], DataItem, Float> index;

    private int maxItemCount = 100;
    private int m = 12;
    private int efConstruction = 250;
    private int ef = 20;
    private DistanceFunction<byte[], Float> distanceFunction = DistanceFunctions.BYTE_COMP;
    private ObjectSerializer<String> itemIdSerializer = new KryoString();
    private ObjectSerializer<DataItem> itemSerializer = new KryoDataItem();

    private DataItem item1 = new DataItem("1", new byte[] { 11,22 }, 10);
    private DataItem item2 = new DataItem("2", new byte[] { 33,44 }, 10);
    private DataItem item3 = new DataItem("3", new byte[] { 55,66 }, 10);

    @BeforeEach
    void setUp() {
        index = HnswIndex
                    .newBuilder(distanceFunction, maxItemCount)
                    .withCustomSerializers(itemIdSerializer, itemSerializer)
                    .withM(m)
                    .withEfConstruction(efConstruction)
                    .withEf(ef)
                    .withRemoveEnabled()
                    .build();
    }

    @Test
    void returnM() {
        assertThat(index.getM(), is(m));
    }

    @Test
    void returnEf() {
        assertThat(index.getEf(), is(ef));
    }

    @Test
    void returnEfConstruction() {
        assertThat(index.getEfConstruction(), is(efConstruction));
    }

    @Test
    void returnMaxItemCount() {
        assertThat(index.getMaxItemCount(), is(maxItemCount));
    }

    @Test
    void returnDistanceFunction() {
        assertThat(index.getDistanceFunction(), is(sameInstance(distanceFunction)));
    }

    @Test
    void returnsItemIdSerializer() { assertThat(index.getItemIdSerializer(), is(sameInstance(itemIdSerializer))); }

    @Test
    void returnsItemSerializer() { assertThat(index.getItemSerializer(), is(sameInstance(itemSerializer))); }

    @Test
    void returnsSize() {
        assertThat(index.size(), is(0));
        index.add(item1);
        assertThat(index.size(), is(1));
    }

    @Test
    void addAndGet() {
        assertThat(index.get(item1.id()), is(Optional.empty()));
        index.add(item1);
        assertThat(index.get(item1.id()), is(Optional.of(item1)));
    }

    @Test
    void returnsItems() {
        assertThat(index.items().isEmpty(), is(true));
        index.add(item1);
        assertThat(index.items().size(), is(1));
        assertThat(index.items(), hasItems(item1));
    }

    @Test
    void removeItem() {
        index.add(item1);

        assertThat(index.remove(item1.id(), item1.version()), is(true));

        assertThat(index.size(), is(0));
        assertThat(index.items().size(), is(0));
        assertThat(index.get(item1.id()), is(Optional.empty()));

        assertThat(index.asExactIndex().size(), is(0));
        assertThat(index.asExactIndex().items().size(), is(0));
        assertThat(index.asExactIndex().get(item1.id()), is(Optional.empty()));
    }

    @Test
    void addNewerItem() {
        DataItem newerItem = new DataItem(item1.id(), new byte[0], item1.version() + 1);

        index.add(item1);
        index.add(newerItem);

        assertThat(index.size(), is(1));
        assertThat(index.get(item1.id()), is(Optional.of(newerItem)));
    }

    @Test
    void addOlderItem() {
        DataItem olderItem = new DataItem(item1.id(), new byte[0], item1.version() - 1);

        index.add(item1);
        index.add(olderItem);

        assertThat(index.size(), is(1));
        assertThat(index.get(item1.id()), is(Optional.of(item1)));
    }

    @Test
    void removeUnknownItem() {
        assertThat(index.remove("foo", 0), is(false));
    }

    @Test
    void removeWithOldVersionIgnored() {
        index.add(item1);

        assertThat(index.remove(item1.id(), item1.version() - 1), is(false));
        assertThat(index.size(), is(1));
    }

//    @Test
//    void findNearest() throws InterruptedException {
//        index.addAll(Arrays.asList(item1, item2, item3));
//
//        List<SearchResult<DataItem, Float>> nearest = index.findNearest(item1.vector(), 10);
//
//        assertThat(nearest, is(Arrays.asList(
//                SearchResult.create(item1, 0f),
//                SearchResult.create(item3, 0.06521261f),
//                SearchResult.create(item2, 0.11621308f)
//        )));
//    }
//
//    @Test
//    void findNeighbors() throws InterruptedException {
//        index.addAll(Arrays.asList(item1, item2, item3));
//
//        List<SearchResult<DataItem, Float>> nearest = index.findNeighbors(item1.id(), 10);
//
//        assertThat(nearest, is(Arrays.asList(
//                SearchResult.create(item3, 0.06521261f),
//                SearchResult.create(item2, 0.11621308f)
//        )));
//    }

    @Test
    void addAllCallsProgressListener() throws InterruptedException {
        List<ProgressUpdate> updates = new ArrayList<>();

        index.addAll(Arrays.asList(item1, item2, item3), 1,
                (workDone, max) -> updates.add(new ProgressUpdate(workDone, max)), 2);

        assertThat(updates, is(Arrays.asList(
                new ProgressUpdate(2, 3),
                new ProgressUpdate(3, 3)  // emitted because its the last element
        )));
    }

    @Test
    void saveAndLoadIndex() throws IOException {
        ByteArrayOutputStream in = new ByteArrayOutputStream();

        index.add(item1);
        File file = new File("F:/ZNV/test1.bin");
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        //index.save(in);
        index.save(fileOutputStream);
        HnswIndex<String, float[], com.github.jelmerk.knn.TestItem, Float> loadedIndex = HnswIndex.load(new FileInputStream(file));


        //HnswIndex<String, byte[], DataItem, Float> loadedIndex = HnswIndex.load(new ByteArrayInputStream(in.toByteArray()));
        assertThat(loadedIndex.size(), is(1));
    }
}
