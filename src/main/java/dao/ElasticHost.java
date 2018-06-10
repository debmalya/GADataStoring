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
package dao;

import org.apache.http.HttpHost;

public class ElasticHost {
	

    private String host;
    private int port;

   public ElasticHost(String host,int port){
       this.host = host;
       this.port = port;
   }

   public HttpHost getHttpHost(){
       return new HttpHost(host,port);
   }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

}
