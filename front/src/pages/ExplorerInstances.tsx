import React from "react"
import { Container, Row, Col } from "reactstrap";
import NavExplorerModels from "../components/Navs/Navs"
import TabExplorerModels from  "../components/Tab/Tabs"
import useActiveTab from "../hooks/tabHooks"
import useModelsByInstance from "../hooks/modelHooks"

export default function ExplorerInstances(props) {
    const [{activeTab, toggle}] = useActiveTab();
    const { instance, models } = useModelsByInstance(props.match.params)
    
    return (
    <Container className="themed-container" fluid={true}>
        <Row>
            <Col className="col-2">
                <NavExplorerModels activeTab={activeTab}
                    toggle={toggle}
                    instance={instance}
                    models={models}
                />
            </Col>
            <Col>
                <TabExplorerModels activeTab={activeTab}
                    instance={instance}
                    models={models}
                />
            </Col>
        </Row>
    </Container>
    )
}