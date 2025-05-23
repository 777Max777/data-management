import React from "react"
import { Row, Col, Label, Input, Button } from "reactstrap";
import { InstanceData, ProtoInstance } from "../../models/instance";
import useInstanceDataForm from "../../hooks/instanceHooks"
import { useResolveTypes } from "../../hooks/resolveTypes"
import {ExplorerPaths, BASE_URL} from "../../services/managementService";
import useAxios from "axios-hooks";

const styles = {
    mgB1: {
      marginBottom: '1rem'
    },
    button: {
        margin: "0 5px 5px 0",
    },
  }
interface FormFieldsProps {
    columns: number, 
    protoInstance: ProtoInstance
}

export default function FormFields({
    columns, 
    protoInstance
}: FormFieldsProps) {
    const [{ data: instancesData }] = useAxios<InstanceData[]>(
    {
        baseURL: BASE_URL,
        url: ExplorerPaths.INSTANCE_VALUES_BY_ID + protoInstance.id,
        method: "GET",
    });
    const {instance, setInstance, updateDataHandler, updateRelationHandler} = useInstanceDataForm(instancesData, protoInstance.name);

    const [{}, updateInstance] = useAxios<InstanceData[]>(
    {
        url: ExplorerPaths.INSTANCES_BY_MODEL_ID, 
        baseURL: BASE_URL,
        method: "PUT"
    },{ manual: true });

    function transformBody(body) {
        return Object.fromEntries(Object.entries(body).map((value, key) => {
          if (value[1] instanceof Map) {
            let obj = Object.create(null);
            value[1].forEach((value, key) => obj[key] = value)
            value[1] = obj
          }
          return value;
      }))
    }

    const onClickSaveHandler = () => {
        instance.id = protoInstance.id
        updateInstance({
            data: transformBody(instance)
        })
    }

    const resolvedFieldElements = useResolveTypes({
        data: instancesData,
        instance,
        onUpdateDataHandler: updateDataHandler,
        onUpdateRelationHandler: updateRelationHandler
    });

    const resolvedTypesByColumns = React.useMemo(() => {
        let mapColumns = new Map<number, Array<any>>();
        function rowWrap(data) {
            return (<Row>{data}</Row>)
        }
        function colWrap(data) {
            return (<Col>{data}</Col>)
        }

        const reducer = (acc, current) => <>{acc}{current}</>

        function putElements(arrElements: Array<any>) {
            arrElements.forEach((element, index) => {
                let values = mapColumns.get(index)
                if (!values || values == null) {
                    values = new Array<any>();
                }
                values.push(element);
                mapColumns.set(index, values)
            })
        }
        let isContinue: boolean = !!(resolvedFieldElements && Array.isArray(resolvedFieldElements) && resolvedFieldElements.length > 0);
        if (!isContinue) {
            return
        }
        for (let i = 0; isContinue; i=i+columns) {
            let arrValues = resolvedFieldElements!.slice(i, i+columns);
            if (arrValues.length > 0) {
                putElements(arrValues);
            }
            if (arrValues.length < columns) {
                isContinue = false
            }
        }
        let arrColumns = new Array();
        mapColumns.forEach((array, key) => {
            let column = array.reduce(reducer);
            arrColumns.push(colWrap(column));
        })
        return rowWrap(arrColumns.reduce(reducer));
    }, [columns, instancesData, resolvedFieldElements])

    return (<>
        <Row>
            <Col>
                <Button color="success" style={styles.button} onClick={onClickSaveHandler}>Сохранить</Button>
            </Col>
        </Row>
        <Row>
            <Col>
                <Label>Название</Label>
                <Input 
                    type={"text"}
                    value={instance.name ? instance.name : ""}
                    style={styles.mgB1}
                    onChange={(event) => setInstance({
                    ...instance,
                    name: event.target.value
                    })}
                />
            </Col>
        </Row>
        {resolvedTypesByColumns && resolvedTypesByColumns}
    </>)
}