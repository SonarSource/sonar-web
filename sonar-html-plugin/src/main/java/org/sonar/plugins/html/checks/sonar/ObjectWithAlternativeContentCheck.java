/*
 * SonarHTML :: SonarQube Plugin
 * Copyright (c) 2010-2019 SonarSource SA and Matthijs Galesloot
 * sonarqube@googlegroups.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sonar.plugins.html.checks.sonar;

import java.util.List;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;

@Rule(key = "S5264")
public class ObjectWithAlternativeContentCheck extends AbstractPageCheck {

  private static class ObjectTag {

    TagNode node;
    boolean hasAlternativeContent;
    
    public ObjectTag(TagNode node) {
      this.node = node;
      this.hasAlternativeContent = false;
    }
  }

  private ObjectTag object;

  @Override
  public void startDocument(List<Node> nodes) {
    object = null;
  }

  @Override
  public void endDocument() {
    object = null;
  }

  @Override
  public void startElement(TagNode node) {
    if (object != null) {
      object.hasAlternativeContent = true;
    }
    if (isObject(node)) {
      object = new ObjectTag(node);
    }
  }

  @Override
  public void endElement(TagNode node) {
    if (isObject(node) && object != null) {
      if (!object.hasAlternativeContent) {
        createViolation(object.node.getStartLinePosition(), "Add an accessible content to this \"<object>\" tag.");
      }
      object = null;
    }
  }

  @Override
  public void characters(TextNode textNode) {
    if (!textNode.isBlank() && object != null) {
      object.hasAlternativeContent = true;
    }
  }
  
  private static boolean isObject(TagNode node) {
    return "OBJECT".equalsIgnoreCase(node.getNodeName());
  }
}