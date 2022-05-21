
package me.cum.fusion.manager;

import me.cum.fusion.features.modules.combat.*;
import java.util.*;

public class ThreadManager
{
    private static final TooBeeCrystalAura ac;
    private final ClientService clientService;
    private static final Queue<Runnable> clientProcesses;
    
    public ThreadManager() {
        (this.clientService = new ClientService()).setDaemon(true);
        this.clientService.start();
    }
    
    static {
        ac = new TooBeeCrystalAura();
        clientProcesses = new ArrayDeque<Runnable>();
    }
    
    public static class ClientService extends Thread
    {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    try {
                        Thread.yield();
                        if (ThreadManager.clientProcesses.size() <= 0) {
                            continue;
                        }
                        ThreadManager.clientProcesses.poll().run();
                        ThreadManager.clientProcesses.remove();
                    }
                    catch (Exception exception) {
                        System.out.println("[noot logger]: thread exception wtf!!");
                        exception.printStackTrace();
                    }
                }
                catch (Exception exception) {
                    System.out.println("[noot logger]: thread exception wtf!!");
                }
            }
        }
        
        public void submit(final Runnable in) {
            ThreadManager.clientProcesses.add(in);
        }
    }
}
