package org.infinispan.jcache;

import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.Set;

import javax.cache.CacheWriter;

import org.infinispan.container.entries.InternalCacheEntry;
import org.infinispan.loaders.AbstractCacheStore;
import org.infinispan.loaders.CacheLoaderConfig;
import org.infinispan.loaders.CacheLoaderException;

public class JCacheWriterAdapter<K, V> extends AbstractCacheStore {

   private CacheWriter<K, V> delegate;

   public JCacheWriterAdapter() {
      // Empty constructor required so that it can be instantiated with
      // reflection. This is a limitation of the way the current cache
      // loader configuration works.
   }

   public void setCacheWriter(CacheWriter<K, V> delegate) {
      this.delegate = delegate;
   }

   @Override
   public void store(InternalCacheEntry entry) throws CacheLoaderException {
      delegate.write(new JCacheEntry(entry.getKey(), entry.getValue()));
   }

   @Override
   public void fromStream(ObjectInput inputStream) throws CacheLoaderException {
      // TODO
   }

   @Override
   public void toStream(ObjectOutput outputStream) throws CacheLoaderException {
      // TODO
   }

   @Override
   public void clear() throws CacheLoaderException {
      // TODO
   }

   @Override
   public boolean remove(Object key) throws CacheLoaderException {
      delegate.delete(key);
      return false;
   }

   @Override
   public InternalCacheEntry load(Object key) throws CacheLoaderException {      
      //TODO
      return null;
   }

   @Override
   public Set<InternalCacheEntry> loadAll() throws CacheLoaderException {
      // TODO
      return Collections.emptySet();
   }

   @Override
   public Set<InternalCacheEntry> load(int numEntries) throws CacheLoaderException {
      // TODO
      return Collections.emptySet();
   }

   @Override
   public Set<Object> loadAllKeys(Set<Object> keysToExclude) throws CacheLoaderException {
      // TODO
      return Collections.emptySet();
   }

   @Override
   public Class<? extends CacheLoaderConfig> getConfigurationClass() {
      // TODO
      return null;
   }

   @Override
   protected void purgeInternal() throws CacheLoaderException {
      // TODO
   }
}
