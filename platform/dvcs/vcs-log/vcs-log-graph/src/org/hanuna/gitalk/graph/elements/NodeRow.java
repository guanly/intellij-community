package org.hanuna.gitalk.graph.elements;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author erokhins
 */
public interface NodeRow {

  @NotNull
  List<Node> getNodes();

  int getRowIndex();
}
