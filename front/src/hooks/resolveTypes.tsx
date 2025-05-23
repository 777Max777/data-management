import React from "react"
import Select from "react-dropdown-select";
import { Input, Label } from "reactstrap";
import { Type } from "../models/Field";
import { Instance, InstanceData } from "../models/instance";
import { ExplorerPaths, BASE_URL } from "../services/managementService";
import Async from 'react-async';

const stylesModalInstance = {
  mgB1: {
    marginBottom: "1rem",
  },
};
type Option = {
    name: string;
    value: string;
}
export interface TypedFieldProps {
  data: InstanceData;
  instance: Instance;
  onUpdateDataHandler(value, id: number): void;
  onUpdateRelationHandler(value, id: number): void;
}

function useResolvingTypes(
  onUpdateDataHandler,
  onUpdateRelationHandler
) {
  const booleanOptions: Option[] = [
    {
      name: "TRUE",
      value: "true"
    },
    {
      name: "FALSE",
      value: "false"
    },
  ];
  const prevRelations = React.useRef<Promise<Option[]>>()

  const loadRelationInstancesCallback = React.useCallback((instanceData: InstanceData) => {
    if (instanceData && instanceData.id && (
      instanceData.fieldType == Type.RELATION || 
      instanceData.fieldType == Type.MULTIPLE_RELATION)) {
        const promise = new Promise<Option[]>((resolve, reject) => {
          fetch(`${BASE_URL}${ExplorerPaths.REALTIONS_BY_FIELD_ID}${instanceData.id}`)
            .then((response) => response.json())
            .then((json) => {
              resolve(
                json.map(({ id, name }) => {
                  return {
                    name: name,
                    value: String(id),
                  };
                })
              );
            })
            .catch(reject);
        });
        prevRelations.current = promise
        return prevRelations.current;
      } else {
        return prevRelations.current;
      }
  }, [])
/*
  const loadRelationInstances = React.useMemo(() => {
    return loadRelationInstancesCallback(instanceData)
  }, [instanceData]);*/

  const resolveType = React.useCallback(async (field: InstanceData,
    instance: Instance,
    promiseRelations?: Promise<Option[]> ) => {
    if (field && field.id) {
      let value = instance.data!.get(field.id);
      if (field.fieldType == Type.STRING) {
        return (
          <div>
            <Label>{field.name}</Label>
            <Input
              type={"text"}
              value={value ? value : ""}
              style={stylesModalInstance.mgB1}
              onChange={(event) =>
                onUpdateDataHandler(event.target.value, field.id!)
              }
            />
          </div>
        );
      } else if (field.fieldType == Type.BOOLEAN) {
        const optionValues = value
        ? new Array(booleanOptions.find((o) => o.value == value)!)
        : []
        return (
          <div>
            <Label>{field.name}</Label>
            <div style={stylesModalInstance.mgB1}>
              <Select
                options={booleanOptions}
                values={optionValues}
                labelField="name"
                valueField="value"
                onChange={(event) => Array.isArray(event) && event[0] &&
                   onUpdateDataHandler(event[0].value, field.id!)}
              />
            </div>
          </div>
        );
      } else if (field.fieldType == Type.NUMBER) {
        return (
          <div>
            <Label>{field.name}</Label>
            <Input
              type={"text"}
              value={value ? value : ""}
              style={stylesModalInstance.mgB1}
              pattern="[0-9]*"
              onChange={(event) =>
                onUpdateDataHandler(
                  event.target.validity.valid ? event.target.value : value,
                  field.id!
                )
              }
            />
          </div>
        );
      } else if (field.fieldType == Type.RELATION || field.fieldType == Type.MULTIPLE_RELATION) {
        let relValues: number[] | undefined;
        instance.relations!.forEach((rels, key) => {
          if (key == field.id) {
            relValues = rels;
          }
        })
        const options = promiseRelations 
            ? await promiseRelations 
            : await loadRelationInstancesCallback(field)
        if (options && Array.isArray(options)) {
          const optionValues = relValues
            ? options.filter((o) => relValues!.includes(Number(o.value)))
            : [];
            if (field.fieldType == Type.RELATION) {
              return (
                <div>
                  <Label>{field.name}</Label>
                  <div style={stylesModalInstance.mgB1}>
                    <Select
                      options={options}
                      values={optionValues}
                      clearable={true}
                      labelField="name"
                      valueField="value"
                      onChange={(event) => Array.isArray(event) && onUpdateRelationHandler(event, field.id!)
                      }
                    />
                  </div>
                </div>
            )
            } else {
                return (
                    <div>
                      <Label>{field.name}</Label>
                      <div style={stylesModalInstance.mgB1}>
                        <Select
                          multi
                          options={options}
                          values={optionValues}
                          labelField="name"
                          valueField="value"
                          onChange={(event) => Array.isArray(event) && onUpdateRelationHandler(event, field.id!)
                          }
                        />
                      </div>
                    </div>
                )
            }
          }
      }
    }
  }, [onUpdateDataHandler, onUpdateRelationHandler])
  return {
    resolveType,
    loadRelationInstancesCallback
  }
}
export default function useResolveType({
  data: instanceData,
  instance,
  onUpdateDataHandler,
  onUpdateRelationHandler
}: TypedFieldProps) {
  const {resolveType} = useResolvingTypes(onUpdateDataHandler, onUpdateRelationHandler);

  return resolveType(instanceData, instance);
}


export interface TypedFieldsProps {
  data: InstanceData[];
  instance: Instance;
  onUpdateDataHandler(value, id: number): void;
  onUpdateRelationHandler(value, id: number): void;
}
export function useResolveTypes({
  data: instancesData,
  instance,
  onUpdateDataHandler,
  onUpdateRelationHandler
}:TypedFieldsProps) {

  const {resolveType} = useResolvingTypes(onUpdateDataHandler, onUpdateRelationHandler);

  const resolvedTypes = React.useMemo(() => {
    if (instancesData && instancesData.length != 0) {
      return instancesData.map(i => {
        return (
          <Async promiseFn={() => resolveType(i, instance)}>
            {({ data }) => {
              if (data) {
                return data;
              }
            }
          }
          </Async>)
      })
    }
  }, [instancesData, onUpdateDataHandler, onUpdateRelationHandler])

  return resolvedTypes;
}