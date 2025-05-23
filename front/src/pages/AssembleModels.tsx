import React from "react";
import { ModelTool } from "../components/Tree/Tree";
import { Container, Row, Col } from "reactstrap";
import { FieldTool } from "../components/Table/Table";
import { ModelToolContext } from "../context/ToolbarContext";
import { useCurrentItem } from "../hooks/toolbarHooks";

const styles = {
  container: {
    marginTop: "30px",
  },
  paragrath: {
    height: "135px",
  },
  box: {
    height: "80vh",
  },
};

export default function AssembleModels() {
  const modelToolContext = useCurrentItem();

  return (
    <ModelToolContext.Provider value={modelToolContext}>
      <Container>
        <Row>
          <Col md="4" style={styles.box}>
            <ModelTool />
          </Col>
          <Col md="8" style={styles.box}>
            <FieldTool />
          </Col>
        </Row>
      </Container>
    </ModelToolContext.Provider>
  );
}
