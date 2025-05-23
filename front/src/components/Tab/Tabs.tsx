import React from "react"
import { TabContent, TabPane, Card, Row } from "reactstrap";
import { TableInstance } from "../Table/Table";
import { ProtoInstance } from "../../models/instance"
import { Entity } from "../../models/Entity"
import  FormFields from "../Fields/FormFields"

interface TabExplorerModelsProps {
    activeTab?: number;
    instance?: ProtoInstance;
    models?: Entity[]
}
export default function TabExplorerModels({
    activeTab, instance, models
}: TabExplorerModelsProps) {
    return (
        <TabContent activeTab={activeTab}>
              {instance &&
                (<TabPane tabId={0}>
                        <Row>
                        <Card body>
                            <FormFields 
                              columns={3} 
                              protoInstance={instance} 
                            />
                        </Card>
                        </Row>
                    </TabPane>)
              }
              {models && models.length != 0 && 
                models.map((e) => (
                  <TabPane key={e.id} tabId={e.id}>
                    <Row>
                      <Card body>
                        <TableInstance modelId={e.id} 
                          activeTabNumber={activeTab} 
                        />
                      </Card>
                    </Row>
                  </TabPane>
                ))}
              </TabContent>
    )
}