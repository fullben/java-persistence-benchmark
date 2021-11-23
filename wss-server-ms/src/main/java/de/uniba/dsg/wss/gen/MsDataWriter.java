package de.uniba.dsg.wss.gen;

import de.uniba.dsg.wss.commons.Stopwatch;
import de.uniba.dsg.wss.data.gen.DataWriter;
import de.uniba.dsg.wss.data.model.ms.MsDataRoot;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Can be used to write a wholesale supplier data model to MicroStream-based storage via the JACIS
 * stores.
 *
 * @author Benedikt Full, Johannes Manner
 */
@Component
public class MsDataWriter implements DataWriter<MsDataConverter> {

    private static final Logger LOG = LogManager.getLogger(MsDataWriter.class);

    private final EmbeddedStorageManager storageManager;
    private final MsDataRoot msDataRoot;

    @Autowired
    public MsDataWriter(EmbeddedStorageManager storageManager, MsDataRoot dataRoot) {
        this.storageManager = storageManager;
        this.msDataRoot = dataRoot;
    }

    @Override
    public void writeAll(MsDataConverter converter) {
        Stopwatch stopwatch = new Stopwatch(true);

        this.msDataRoot.getWarehouses().putAll(converter.getWarehouses());
        this.msDataRoot.getEmployees().putAll(converter.getEmployees());
        this.msDataRoot.getCustomers().putAll(converter.getCustomers());
        this.msDataRoot.getStocks().putAll(converter.getStocks());
        this.msDataRoot.getOrders().putAll(converter.getOrders());
        this.msDataRoot.getCarriers().putAll(converter.getCarriers());
        this.msDataRoot.getProducts().putAll(converter.getProducts());
        this.storageManager.setRoot(this.msDataRoot);
        this.storageManager.storeRoot();

        stopwatch.stop();
        LOG.info("Wrote model data to MicroStream storage, took {}", stopwatch.getDuration());
    }
}
