/**
 * Copyright 2018-2019 Debmalya Jash
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledRetriever {
	
	    public static void main(String[] args){
	    	LatitudeLongitudeRetriever latitudeLongitudeRetriever = new LatitudeLongitudeRetriever();
	    	new ScheduledRetriever(latitudeLongitudeRetriever, 0, 30);
	    }
	    /**
	     * 
	     * @param runnable
	     * @param initialDelay
	     * @param minute
	     */
		public ScheduledRetriever(Runnable runnable,int initialDelay, int minute){
			ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	        scheduler.scheduleAtFixedRate(new Thread(runnable), initialDelay, minute, TimeUnit.MINUTES);
		}
}
