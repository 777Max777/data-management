import React from "react";
import SelectSearch, { SelectedOptionValue, SelectSearchOption } from "react-select-search";
import "./styles.css";
import {
  Button,
  Modal,
  ModalHeader,
  ModalBody,
  ModalFooter,
  Input,
  Label
} from "reactstrap";
import { Entity } from "../../models/Entity";
import { Field, Type } from "../../models/Field";
import { Instance, InstanceData } from "../../models/instance";
import useAxios from "axios-hooks";
import { ManagementPaths, ExplorerPaths, BASE_URL } from "../../services/managementService";
import TypedField from "../Fields/TypedField"
import useInstanceDataForm from "../../hooks/instanceHooks"

const styles = {
  mtop1: {
    marginTop: "1rem",
  },
};

type ModalEntityProps = {
  modal: boolean;
  text: string;
  data?: Entity;
  onHandle(newModel: Entity): void;
  toggle(): void;
  parentModels?: Entity[];
};

type ModalFieldProps = {
  modal: boolean;
  text: string;
  data?: Field;
  onHandle(newField: Field): void;
  toggle(): void;
};

type ModalInstanceProps = {
  modal: boolean;
  text: string;
  data: InstanceData[];
  instanceName?: string;
  onHandle(newInstance: Instance): void;
  toggle(): void;
};

export const ModalEntity: React.FC<ModalEntityProps> = ({
  modal,
  onHandle,
  text,
  toggle,
  data,
  parentModels,
}) => {
  function init(): Entity {
    if (data) {
      return data;
    } else {
      return {
        id: 555,
        name: "",
        description: "",
      };
    }
  }

  const [newModel, setNewModel] = React.useState(init);

  React.useEffect(() => {
    setNewModel(init);
  }, [data]);

  function selectSearchHandler(
    selectedValue: SelectedOptionValue | SelectedOptionValue[]
  ) {
    if (!Array.isArray(selectedValue)) {
      setNewModel({
        id: newModel.id,
        name: newModel.name,
        description: newModel.description,
        parent: {
          id: Number(selectedValue),
        },
      });
    }
  }

  return (
    <Modal isOpen={modal} toggle={toggle}>
      <ModalHeader toggle={toggle}>{text}</ModalHeader>
      <ModalBody>
        <Input
          type={"text"}
          required
          placeholder={"Введите имя модели"}
          value={newModel.name}
          onChange={(event) =>
            setNewModel({
              id: newModel.id,
              name: event.target.value,
              description: newModel.description,
              parent: newModel.parent,
            })
          }
        />
        <Input
          type={"textarea"}
          placeholder={"Опишите модель"}
          style={styles.mtop1}
          value={newModel.description}
          onChange={(event) => {
            setNewModel({
              id: newModel.id,
              name: newModel.name,
              description: event.target.value,
              parent: newModel.parent,
            });
          }}
        />
        {parentModels && (
          <SelectSearch
            options={parentModels.map((m) => ({
              name: m.name,
              value: String(m.id),
            }))}
            search
            placeholder={"Выберите родителя"}
            onChange={(event) => selectSearchHandler(event)}
          />
        )}
      </ModalBody>
      <ModalFooter>
        <Button color="primary" onClick={() => onHandle(newModel)}>
          Ок
        </Button>
        <Button color="secondary" onClick={toggle}>
          Закрыть
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export const ModalField: React.FC<ModalFieldProps> = ({
  modal,
  onHandle,
  text,
  toggle,
  data,
}) => {
  function init(): Field {
    if (data) {
      return data;
    } else {
      return {
        name: "",
        fieldType: Type.STRING,
      };
    }
  }

  const [newField, setNewField] = React.useState(init);
  const [{ data: baseEntities, loading, error }, refetch] = useAxios(
    {
      url: ManagementPaths.BASE_MODEL,
      baseURL: BASE_URL,
      method: "GET",
    },
    { manual: true }
  );

  React.useEffect(() => {
    setNewField(init);
  }, [data]);

  function selectSearchHandler(selectedValue: any) {
    if (!Array.isArray(selectedValue)) {
      setNewField({
        ...newField,
        fieldType: Type[selectedValue],
      });
      if (
        Type[selectedValue] == Type.MULTIPLE_RELATION ||
        Type[selectedValue] == Type.RELATION
      ) {
        refetch();
      }
    }
  }

  function selectRelationHandler(selectedValue: any) {
    if (!Array.isArray(selectedValue)) {
      setNewField({
        ...newField,
        relEntity: {
          id: selectedValue,
          name: "",
        },
      });
    }
  }

  return (
    <Modal isOpen={modal} toggle={toggle}>
      <ModalHeader toggle={toggle}>{text}</ModalHeader>
      <ModalBody>
        <Input
          type={"text"}
          required
          placeholder={"Введите имя поля"}
          value={newField.name}
          onChange={(event) =>
            setNewField({
              ...newField,
              name: event.target.value,
            })
          }
        />
        <SelectSearch
          options={Object.keys(Type).map((key) => ({
            name: Type[Type[key]],
            value: Type[key],
          }))}
          placeholder={"Выберите поле"}
          onChange={(event) => selectSearchHandler(event)}
        />
        {newField.fieldType &&
          (newField.fieldType == Type.RELATION ||
            newField.fieldType == Type.MULTIPLE_RELATION) &&
          baseEntities &&
          Array.isArray(baseEntities) && (
            <SelectSearch
              options={baseEntities.map((e: Entity) => ({
                name: e.name,
                value: String(e.id),
              }))}
              placeholder={"Выберите ссылку"}
              onChange={(event) => selectRelationHandler(event)}
            />
          )}
      </ModalBody>
      <ModalFooter>
        <Button color="primary" onClick={() => onHandle(newField)}>
          Ок
        </Button>
        <Button color="secondary" onClick={toggle}>
          Закрыть
        </Button>
      </ModalFooter>
    </Modal>
  );
};

const stylesModalInstance = {
  mgB1: {
    marginBottom: '1rem'
  }
}
export const ModalInstance: React.FC<ModalInstanceProps> = ({
  modal,
  onHandle,
  text,
  toggle,
  data: instanceData,
  instanceName,
}) => {

  const {instance, setInstance, updateDataHandler, updateRelationHandler} = useInstanceDataForm(instanceData, instanceName);
  
  return (
    <Modal isOpen={modal} toggle={toggle}>
      <ModalHeader toggle={toggle}>{text}</ModalHeader>
      <ModalBody>
        <Label>Название</Label>
        <Input 
            type={"text"}
            value={instance.name ? instance.name : ""}
            style={stylesModalInstance.mgB1}
            onChange={(event) => setInstance({
              ...instance,
              name: event.target.value
            })}
          />
          {instance && instanceData && instanceData.map((field, index) => {
            return (<TypedField key={index} data={field}
              instance={instance}
              onUpdateDataHandler={updateDataHandler}
              onUpdateRelationHandler={updateRelationHandler}
          />)
        })
        }
      </ModalBody>
      <ModalFooter>
        <Button color="primary" onClick={() => onHandle(instance)}>
          Ок
        </Button>
        <Button color="secondary" onClick={toggle}>
          Закрыть
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default ModalEntity;
