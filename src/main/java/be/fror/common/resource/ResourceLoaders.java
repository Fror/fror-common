/*
 * Copyright 2015 Olivier Grégoire <https://github.com/ogregoire>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.fror.common.resource;

import com.google.common.io.ByteSource;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Olivier Grégoire &lt;https://github.com/ogregoire&gt;
 */
public class ResourceLoaders {

  private ResourceLoaders() {
  }

  public static ResourceLoader<Properties> propertiesLoader() {
    return StandardPropertiesLoader.PROPERTIES;
  }

  public static ResourceLoader<Properties> xmlPropertiesLoader() {
    return StandardPropertiesLoader.XML;
  }

  private enum StandardPropertiesLoader implements ResourceLoader<Properties> {

    PROPERTIES {
          @Override
          public Properties load(ByteSource source) throws IOException {
            Properties properties = new Properties();
            properties.load(source.openStream());
            return properties;
          }

        },
    XML {
          @Override
          public Properties load(ByteSource source) throws IOException {
            Properties properties = new Properties();
            properties.loadFromXML(source.openStream());
            return properties;
          }
        };
  }

  private static final Map<Charset, ResourceLoader<Properties>> charsetPropertiesLoader = new ConcurrentHashMap<>();

  public static ResourceLoader<Properties> propertiesLoader(Charset charset) {
    return charsetPropertiesLoader.computeIfAbsent(charset, (c) -> new CharsetPropertiesLoader(c));
  }

  private static class CharsetPropertiesLoader implements ResourceLoader<Properties> {

    private Charset charset;

    private CharsetPropertiesLoader(Charset charset) {
      this.charset = charset;
    }

    @Override
    public Properties load(ByteSource source) throws IOException {
      Properties properties = new Properties();
      properties.load(source.asCharSource(charset).openStream());
      return properties;
    }

  }

}