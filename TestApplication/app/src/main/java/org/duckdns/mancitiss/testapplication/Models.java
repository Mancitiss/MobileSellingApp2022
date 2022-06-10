package org.duckdns.mancitiss.testapplication;

import org.duckdns.mancitiss.testapplication.entities.Foods;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import kotlinx.coroutines.internal.ConcurrentLinkedListKt;

public class Models{
        private static Models instance;

        private boolean isInitialized = false;

        private ConcurrentHashMap<String, Product> knownProducts;
        private ConcurrentHashMap<String, Integer> shoppingCart;
        private List<String> notifications;
        public Foods currentFood;

        public String currentname;
        public String currentphone;
        public String currentaddress;


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
                shoppingCart = new ConcurrentHashMap<String, Integer>();
                notifications = new ArrayList<String>();
                isInitialized = true;
            }
        }

        public ConcurrentHashMap<String, Product> getKnownProducts(){
            return knownProducts;
        }
        public ConcurrentHashMap<String, Integer> getShoppingCart(){
            return shoppingCart;
        }
        public List<String> getNotifications(){
            return notifications;
        }

}
