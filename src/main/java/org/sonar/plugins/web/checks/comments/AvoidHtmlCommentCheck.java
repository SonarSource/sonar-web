/*
 * SonarQube Web Plugin
 * Copyright (C) 2010 SonarSource and Matthijs Galesloot
 * dev@sonar.codehaus.org
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
package org.sonar.plugins.web.checks.comments;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.RuleTags;
import org.sonar.plugins.web.checks.WebRule;
import org.sonar.plugins.web.node.CommentNode;
import org.sonar.plugins.web.node.Node;

import java.util.Iterator;
import java.util.List;

/**
 * Checker for occurrence of html comments.
 *
 * HTML comment is not allowed in JSP and other server side pages, use server side comment instead.
 */
@Rule(key = "AvoidHtmlCommentCheck", priority = Priority.MINOR)
@WebRule(activeByDefault = false)
@RuleTags({
  RuleTags.SECURITY
})
public class AvoidHtmlCommentCheck extends AbstractPageCheck {

  private boolean isServerSidePage;

  @Override
  public void comment(CommentNode node) {
    String comment = node.getCode();

    if (isServerSidePage && node.isHtml() && !comment.startsWith("<!--[if")) {
      createViolation(node.getStartLinePosition(), "Remove this HTML comment.");
    }
  }

  @Override
  public void startDocument(List<Node> nodes) {
    isServerSidePage = false;
    Iterator<Node> iterator = nodes.iterator();
    while (!isServerSidePage && iterator.hasNext()) {
      String code = iterator.next().getCode();
      if (code.startsWith("<?php") || code.startsWith("<%")) {
        isServerSidePage = true;
      }
    }
  }

}
