package org.duckdns.mancitiss.testapplication;

import java.util.concurrent.ConcurrentHashMap;

public class Models{
        private static Models instance;

        private boolean isInitialized = false;

        private ConcurrentHashMap<String, Product> knownProducts;

        private Models() {
        }

        public static synchronized Models getInstance() {
            if (instance == null) {
                instance = new Models();
                instance.loadModels();
            }
            return instance;
        }

        public void loadModels() {
            if (!isInitialized) {
                /*
                 *  One-time model initialization here.
                 */
                knownProducts = new ConcurrentHashMap<String, Product>();
                isInitialized = true;
            }
        }

        public ConcurrentHashMap<String, Product> getKnownProducts(){
            return knownProducts;
        }
}
