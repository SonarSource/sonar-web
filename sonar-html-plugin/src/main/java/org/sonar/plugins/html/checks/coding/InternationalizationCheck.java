/*
 * SonarHTML :: SonarQube Plugin
 * Copyright (c) 2010-2018 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.html.checks.coding;

import org.apache.commons.lang.StringUtils;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;

import java.util.List;

@Rule(key = "InternationalizationCheck")
public class InternationalizationCheck extends AbstractPageCheck {

  private static final String PUNCTUATIONS_AND_SPACE = " \t\n\r|-%:,.?!/,'\"";
  private static final String DEFAULT_ATTRIBUTES = "outputLabel.value, outputText.value";

  @RuleProperty(
    key = "attributes",
    description = "Attributes",
    defaultValue = DEFAULT_ATTRIBUTES)
  public String attributes = DEFAULT_ATTRIBUTES;

  private QualifiedAttribute[] attributesArray;

  @Override
  public void startDocument(List<Node> nodes) {
    this.attributesArray = parseAttributes(attributes);
  }

  @Override
  public void characters(TextNode textNode) {
    if (!isUnifiedExpression(textNode.getCode()) && !isPunctuationOrSpace(textNode.getCode())) {
      createViolation(textNode.getStartLinePosition(), "Define this label in the resource bundle.");
    }
  }

  @Override
  public void startElement(TagNode element) {
    if (attributesArray.length > 0) {
      for (QualifiedAttribute attribute : attributesArray) {
        if (notValid(element, attribute)) {
          return;
        }
      }
    }
  }

  private boolean notValid(TagNode element, QualifiedAttribute attribute) {
    if (element.equalsElementName(attribute.getNodeName())) {
      String value = element.getAttribute(attribute.getAttributeName());
      if (value != null) {
        value = value.trim();
        if (value.length() > 0 && !isUnifiedExpression(value) && !isPunctuationOrSpace(value)) {
          createViolation(element.getStartLinePosition(), "Define this label in the resource bundle.");
          return true;
        }
      }
    }
    return false;
  }

  private static boolean isPunctuationOrSpace(String value) {
    return StringUtils.containsAny(value, PUNCTUATIONS_AND_SPACE);
  }

}