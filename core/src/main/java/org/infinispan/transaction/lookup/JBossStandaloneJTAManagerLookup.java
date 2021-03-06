package org.infinispan.transaction.lookup;

import org.infinispan.commons.util.Util;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.factories.annotations.Inject;
import org.infinispan.util.logging.Log;
import org.infinispan.util.logging.LogFactory;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import java.lang.reflect.Method;

/**
 * JTA standalone TM lookup.
 *
 * @author Jason T. Greene
 * @since 4.0
 */
public class JBossStandaloneJTAManagerLookup implements TransactionManagerLookup {
   private Method manager, user;
   private static final Log log = LogFactory.getLog(JBossStandaloneJTAManagerLookup.class);

   /**
    * @deprecated Use {@link #init(org.infinispan.configuration.cache.Configuration)} instead.
    */
   @Deprecated
   public void init(org.infinispan.config.Configuration configuration) {
      init(configuration.getClassLoader());
   }

   @Inject
   public void init(Configuration configuration) {
      init(configuration.classLoader());
   }

   private void init(ClassLoader classLoader) {
      // The TM may be deployed embedded alongside the app, so this needs to be looked up on the same CL as the Cache
      try {
         manager = Util.loadClass("com.arjuna.ats.jta.TransactionManager", classLoader).getMethod("transactionManager");
         user = Util.loadClass("com.arjuna.ats.jta.UserTransaction", classLoader).getMethod("userTransaction");
      } catch (SecurityException e) {
         throw new RuntimeException(e);
      } catch (NoSuchMethodException e) {
         throw new RuntimeException(e);
      }
   }

   @Override
   public synchronized TransactionManager getTransactionManager() throws Exception {
      TransactionManager tm = (TransactionManager) manager.invoke(null);
      if (log.isInfoEnabled()) log.retrievingTm(tm);
      return tm;
   }

   public UserTransaction getUserTransaction() throws Exception {
      return (UserTransaction) user.invoke(null);
   }

   @Override
   public String toString() {
      return "JBossStandaloneJTAManagerLookup";
   }
}