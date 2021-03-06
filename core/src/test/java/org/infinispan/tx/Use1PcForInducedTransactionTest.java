package org.infinispan.tx;

import org.infinispan.commands.tx.CommitCommand;
import org.infinispan.commands.tx.PrepareCommand;
import org.infinispan.config.Configuration;
import org.infinispan.context.impl.TxInvocationContext;
import org.infinispan.interceptors.base.CommandInterceptor;
import org.infinispan.test.MultipleCacheManagersTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test (groups = "functional", testName = "tx.Use1PcForInducedTransactionTest")
public class Use1PcForInducedTransactionTest extends MultipleCacheManagersTest {

   private InvocationCountInterceptor ic0;
   private InvocationCountInterceptor ic1;

   @Override
   protected void createCacheManagers() throws Throwable {
      Configuration c = getDefaultClusteredConfig(Configuration.CacheMode.DIST_SYNC, true);
      c.fluent().transaction().use1PcForAutoCommitTransactions(true);
      assert c.isUse1PcForAutoCommitTransactions();

      createCluster(c, 2);
      waitForClusterToForm();

      ic0 = new InvocationCountInterceptor();
      advancedCache(0).addInterceptor(ic0, 1);
      ic1 = new InvocationCountInterceptor();
      advancedCache(1).addInterceptor(ic1, 1);
   }

   public void testSinglePhaseCommit() {
      cache(0).put("k", "v");
      assert cache(0).get("k").equals("v");
      assert cache(1).get("k").equals("v");

      assertNotLocked("k");

      assertEquals(ic0.prepareInvocations, 1);
      assertEquals(ic1.prepareInvocations, 1);
      assertEquals(ic0.commitInvocations, 0);
      assertEquals(ic0.commitInvocations, 0);
   }


   public static class InvocationCountInterceptor extends CommandInterceptor {

      volatile int prepareInvocations;
      volatile int commitInvocations;

      @Override
      public Object visitPrepareCommand(TxInvocationContext ctx, PrepareCommand command) throws Throwable {
         prepareInvocations ++;
         return super.visitPrepareCommand(ctx, command);
      }

      @Override
      public Object visitCommitCommand(TxInvocationContext ctx, CommitCommand command) throws Throwable {
         commitInvocations ++;
         return super.visitCommitCommand(ctx, command);
      }
   }
}
