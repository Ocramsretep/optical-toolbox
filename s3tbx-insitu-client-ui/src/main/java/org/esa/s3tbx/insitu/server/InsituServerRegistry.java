package org.esa.s3tbx.insitu.server;

import com.bc.ceres.core.ServiceRegistry;
import com.bc.ceres.core.ServiceRegistryManager;
import org.esa.snap.core.util.ServiceLoader;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * @author Marco Peters
 */
public class InsituServerRegistry {

    private ServiceRegistry<InsituServerSpi> registry;

    /**
     * Gets the singleton instance of the registry.
     *
     * @return The instance.
     */
    public static InsituServerRegistry getInstance() {
        return Holder.instance;
    }

    private InsituServerRegistry() {
        registry = ServiceRegistryManager.getInstance().getServiceRegistry(InsituServerSpi.class);
        ServiceLoader.loadServices(registry);
    }

    public Set<InsituServerSpi> getAllRegisteredServers() {
        return Collections.unmodifiableSet(registry.getServices());
    }

    public InsituServerSpi getRegisteredServer(String serverName) {
        final Optional<InsituServerSpi> optional = findSpi(serverName);
        return optional.isPresent() ? optional.get() : null;
    }

    boolean addServer(InsituServerSpi serverSpi) {
        final Optional<InsituServerSpi> first = findSpi(serverSpi.getName());
        return !first.isPresent() && registry.addService(serverSpi);
    }

    boolean removeServer(String serverName) {
        final Optional<InsituServerSpi> first = findSpi(serverName);
        return first.isPresent() && removeServer(first.get());
    }

    boolean removeServer(InsituServerSpi serverSpi) {
        return registry.removeService(serverSpi);
    }

    private Optional<InsituServerSpi> findSpi(String name) {
        return registry.getServices().stream().filter(inSituServerSpi -> inSituServerSpi.getName().equals(name)).findFirst();
    }

    private static class Holder {
        private static final InsituServerRegistry instance = new InsituServerRegistry();
    }

}
