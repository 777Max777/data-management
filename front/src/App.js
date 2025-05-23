import React from "react";
import "./App.css";
import { Container, Row, Col } from "reactstrap";
import HeaderNav from "./components/Header/Header";
import MainRouter from "./components/routers/Router"

export default function App() {
  return (
    <Container className="themed-container" fluid={true}>
      <Row>
        <Col>
          <HeaderNav />
        </Col>
      </Row>
      <Row>
          <MainRouter />
      </Row>
    </Container>
  );
}
