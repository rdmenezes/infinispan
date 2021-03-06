package org.infinispan.container;

import org.infinispan.metadata.Metadata;
import org.infinispan.container.entries.CacheEntry;
import org.infinispan.container.entries.ClusteredRepeatableReadEntry;
import org.infinispan.container.entries.MVCCEntry;
import org.infinispan.container.entries.NullMarkerEntry;
import org.infinispan.container.entries.NullMarkerEntryForRemoval;
import org.infinispan.factories.annotations.Start;
import org.infinispan.metadata.Metadatas;

/**
 * An entry factory that is capable of dealing with SimpleClusteredVersions.  This should <i>only</i> be used with
 * optimistically transactional, repeatable read, write skew check enabled caches in replicated or distributed mode.
 *
 * @author Manik Surtani
 * @since 5.1
 */
public class IncrementalVersionableEntryFactoryImpl extends EntryFactoryImpl {

   @Start (priority = 9)
   public void setWriteSkewCheckFlag() {
      localModeWriteSkewCheck = false;
      useRepeatableRead = true;
   }

   @Override
   protected MVCCEntry createWrappedEntry(Object key, CacheEntry cacheEntry,
         Metadata providedMetadata, boolean isForInsert, boolean forRemoval) {
      Metadata metadata;
      Object value;
      if (cacheEntry != null) {
         value = cacheEntry.getValue();
         Metadata entryMetadata = cacheEntry.getMetadata();
         if (providedMetadata != null && entryMetadata != null) {
            metadata = Metadatas.applyVersion(entryMetadata, providedMetadata);
         } else if (providedMetadata == null) {
            metadata = entryMetadata; // take the metadata in memory
         } else {
            metadata = providedMetadata;
         }
      } else {
         value = null;
         metadata = providedMetadata;
      }

      if (value == null && !isForInsert)
         return forRemoval ? new NullMarkerEntryForRemoval(key, metadata)
               : NullMarkerEntry.getInstance();

      return new ClusteredRepeatableReadEntry(key, value, metadata);
   }

}
