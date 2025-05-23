import React, { MouseEventHandler } from "react";
import { Table } from "reactstrap";
import { Redirect } from "react-router-dom";
import { Field, Type } from "../../models/Field";
import { Instance, InstanceData } from "../../models/instance";
import { BaseToolbar } from "../Toolbar/Toolbar";
import {
  ExplorerPaths,
  ManagementPaths,
  BASE_URL,
} from "../../services/managementService";
import { Container, Row } from "reactstrap";
import useAxios from "axios-hooks";
import { ModalField, ModalInstance } from "../Modal/Modal";
import { Entity } from "../../models/Entity";
import { useToolbar } from "../../hooks/toolbarHooks";
import { ModelToolContext } from "../../context/ToolbarContext";

type TableFieldsProps = {
  body: Field[];
  headers: string[];
  onClick(selected): void;
};
type DataTableFieldsProps = {
  body: Field[];
  entity: Entity;
};
type TableIntanceProps = {
  body: Instance[];
  headers: string[];
  currentInstance?: Instance;
  onClick(selected): void;
  onDoubleClick(selected): void;
};
const styles = {
  choosetItem: {
    backgroundColour: "rgb(1 9 19 / 25%)"
  }
}
export const TableInstances: React.FC<TableIntanceProps> = ({
  body,
  headers,
  currentInstance,
  onClick,
  onDoubleClick
}) => {
  const currentChoosenItem = React.useRef<HTMLTableRowElement>();

  const onClickHandler = React.useCallback((event, id) => {
    event.preventDefault();
    if (currentChoosenItem.current) {
      currentChoosenItem.current.style.backgroundColor = "";
    }
    event.currentTarget.style.backgroundColor = styles.choosetItem.backgroundColour
    currentChoosenItem.current = event.currentTarget;

    onClick(id)
  }, [])

  React.useEffect(() => {
    if (currentInstance == null && currentChoosenItem.current) {
      currentChoosenItem.current.style.backgroundColor = "";
    }
  }, [currentInstance])

  return (
    <Table hover bordered>
      <thead>
        <tr>
          {headers.map((hName, index) => (
            <th key={index}>{hName}</th>
          ))}
        </tr>
      </thead>
      <tbody>
        {body &&
          body.length > 0 &&
          body.map((row, index) => (
            <tr key={index} 
              onDoubleClick={(e) => onDoubleClick(row.id)} 
              onClick={(e) => onClickHandler(e, row.id)}>
              <td>{row.id}</td>
              <td>{row.name}</td>
            </tr>
          ))}
      </tbody>
    </Table>
  );
};

export const TableFields: React.FC<TableFieldsProps> = ({
  body,
  headers,
  onClick,
}) => {
  return (
    <Table hover bordered>
      <thead>
        <tr>
          {headers.map((hName, index) => (
            <th key={index}>{hName}</th>
          ))}
        </tr>
      </thead>
      <tbody>
        {body &&
          body.length > 0 &&
          body.map((row, index) => (
            <tr key={index} onClick={(e) => onClick(row.id)}>
              <td>{row.id}</td>
              <td>{row.name}</td>
              <td>{row.fieldType}</td>
            </tr>
          ))}
      </tbody>
    </Table>
  );
};

export const DataTableFields: React.FC<DataTableFieldsProps> = ({
  body,
  entity,
}) => {
  const headers = ["id", "Название", "Тип"];

  function init(): Field[] {
    if (body) {
      console.log("There are some fields ", body);
      return body;
    }
    console.log("There are no fields");
    return [];
  }

  const [data, setData] = React.useState<Field[]>(init);
  const [createModal, setCreateModal] = React.useState(false);
  const [editModal, setEditModal] = React.useState(false);
  const [checkedField, setCheckedField] = React.useState<Field>({
    id: "",
    name: "",
    fieldType: Type.STRING,
    entities: Array<Entity>(entity),
  });

  const [{ data: fData, loading: fLoading, error: fErr }, refetch] = useAxios(
    {
      url: ManagementPaths.BASE_FIELD,
      baseURL: BASE_URL,
      method: "GET",
    },
    { manual: true }
  );

  React.useEffect(() => {
    setData(init);
  }, [body]);

  React.useEffect(() => {
    if (fData) {
      if (Array.isArray(fData)) {
        setData(fData);
      } else {
        let field: Field | undefined = data.find((x) => x.id === fData.id);
        if (field) {
          Object.assign(field, fData);
        } else {
          data.push(fData);
        }
        setData(data);
      }
    }
  }, [fData]);

  function createToggle(): void {
    setCreateModal((m) => !m);
  }

  function editToggle(): void {
    setEditModal((m) => !m);
  }

  function checkField(id) {
    //let element = {};
    //getDataTableElementById(id, element);
    let field: Field | undefined = data.find((x) => x.id === id);
    field && setCheckedField(field);
  }

  const editModel = (editingField: Field) => {
    editingField.entities = checkedField.entities;
    refetch({
      method: "PUT",
      data: editingField,
    });
    editToggle();
  };

  const createModel = (newField: Field) => {
    newField.entities = Array<Entity>(entity);
    refetch({
      method: "POST",
      data: newField,
    });
    createToggle();
  };

  const removeField = () => {
    refetch({
      method: "DELETE",
      data: checkedField,
    });
    setData(data.filter((x) => x.id !== checkedField.id));
  };

  return (
    <>
      <BaseToolbar
        onCreate={createToggle}
        onChange={editToggle}
        onDelete={removeField}
      />
      <TableFields headers={headers} body={data} onClick={checkField} />
      <ModalField
        toggle={createToggle}
        modal={createModal}
        onHandle={createModel}
        text={"Создать новое поле"}
      />
      <ModalField
        toggle={editToggle}
        modal={editModal}
        onHandle={editModel}
        text={"Изменить поле"}
        data={checkedField}
      />
    </>
  );
};

export function FieldTool() {
  const headers = ["id", "Название", "Тип"];

  const [
    { data, isShowCreate, isShowEdit, loading, error, currentItem },
    {
      onCreateHandler,
      onEditHandler,
      onCheckItemHandler,
      onDeleteHandler,
      fetchData,
      onCloseModalHandler,
      updateData,
    },
  ] = useToolbar(
    {
      url: ManagementPaths.BASE_FIELD,
      baseURL: BASE_URL,
      method: "GET",
    },
    finderPredicate,
    undefined,
    sortTreeNodes,
    { manual: true }
  );

  const { data: currentModelItem, updateData: updateModel } = React.useContext(
    ModelToolContext
  );

  React.useEffect(() => {
    if (currentModelItem && !Array.isArray(currentModelItem)) {
      if (currentModelItem.fields) updateData(currentModelItem.fields);
      else updateData(new Array());
    }
  }, [currentModelItem]);

  React.useEffect(() => {
    if (currentModelItem && !Array.isArray(currentModelItem)) {
      if (data && Array.isArray(data)) currentModelItem.fields = data;
      /*currentModelItem.fields = data;
      updateModel &&
        updateModel(currentModelItem, (x) => x.id === currentModelItem.id);*/
    }
  }, [data]);

  function finderPredicate(
    current: Field
  ): (value: Field, index: number, obj: Field[]) => unknown {
    return (x: Field) => x.id === current.id;
  }

  function sortTreeNodes(dataArray) {
    if (dataArray && Array.isArray(dataArray)) {
      dataArray.sort((a, b) => a.name.localeCompare(b.name));
    }
  }

  function onDeleteClick() {
    currentItem && onDeleteHandler((x) => x.id !== currentItem.id);
  }

  function onTableItemClick(itemId) {
    onCheckItemHandler((x) => x.id === itemId);
  }

  function acceptField(field: Field) {
    if (currentModelItem && !Array.isArray(currentModelItem)) {
      if (!field.entities) {
        field.entities = new Array();
        field.entities.push({
          ...currentModelItem,
        });
      } else if (!field.entities.find((x) => x.id === currentModelItem.id)) {
        field.entities.push({
          ...currentModelItem,
        });
      }
    }
    fetchData(undefined, field);
  }

  return (
    <>
      <BaseToolbar
        onCreate={onCreateHandler}
        onChange={onEditHandler}
        onDelete={onDeleteClick}
      />
      {data && (
        <TableFields headers={headers} body={data} onClick={onTableItemClick} />
      )}
      <ModalField
        toggle={onCloseModalHandler}
        modal={isShowCreate || isShowEdit}
        onHandle={(field: Field) => acceptField(field)}
        text={isShowCreate ? "Создать новое поле" : "Изменить поле"}
        data={isShowEdit ? currentItem : undefined}
      />
    </>
  );
}

interface TableInstanceProps {
  modelId: number;
  activeTabNumber?: number;
}
export function TableInstance({modelId, activeTabNumber}: TableInstanceProps) {
  const headers = ["ID", "Наименование экземпляра"];
  const [
    { data, isShowCreate, isShowEdit, currentItem },
    {
      onCreateHandler,
      onEditHandler,
      onCheckItemHandler,
      onDeleteHandler,
      fetchData,
      onCloseModalHandler,
      updateData
    },
  ] = useToolbar(
    {
      url: ExplorerPaths.INSTANCES_BY_MODEL_ID + modelId,
      baseURL: BASE_URL,
      method: "GET"
    },
    finderPredicate,
    undefined,
    sortDataTable
  );

  const [{ data: protoFields }] = useAxios<Field[]>({
    baseURL: BASE_URL,
    url: ManagementPaths.FIELD_BY_MODEL_ID + modelId,
    method: "GET",
  });

  const [{ data: fieldValues }, refetchInstanceValues] = useAxios<
    InstanceData[]
  >(
    {
      baseURL: BASE_URL,
      method: "GET",
    },
    { manual: true }
  );

  const convertProtoFields = React.useCallback(() => {
    if (protoFields) {
      const newFields: InstanceData[] = protoFields.map((f) => {
        return {
          ...f,
          id: +f.id!,
        };
      });
      return newFields;
    }
  }, [protoFields])

  const [configFields, setConfigFields] = React.useState<
    InstanceData[] | undefined
  >(convertProtoFields);

  const [isRedirect, setIsRedirect] = React.useState<number | undefined>(undefined);
  const [isAvailableModalWindow, setAvailableWindow] = React.useState<boolean>(false);

  React.useEffect(() => {
    if (currentItem) {
      console.log("Current item", currentItem.id);
      refetchInstanceValues({
        url: ExplorerPaths.INSTANCE_VALUES_BY_ID + currentItem.id,
      });
    }
  }, [currentItem]);

  React.useEffect(() => {
    console.log("Got proto Fields")
    setConfigFields(convertProtoFields)
  }, [protoFields]);

  React.useEffect(() => {
    data && updateData(data)
  }, [data])

  function finderPredicate(
    current: Instance
  ): (value: Instance, index: number, obj: Instance[]) => unknown {
    return (x: Instance) => x.id === current.id;
  }

  function sortDataTable(dataArray) {
    if (dataArray && Array.isArray(dataArray)) {
      dataArray.sort((a, b) => b.id - a.id);
    }
  }

  function onDeleteClick() {
    currentItem && fetchData((x) => x.id !== currentItem.id, undefined, 
    {
      url: ExplorerPaths.INSTANCES_BY_MODEL_ID, 
      baseURL: BASE_URL
    });
  }

  function onTableItemClick(itemId) {
    onCheckItemHandler((x) => x.id === itemId);
  }

  function onTableItemDoubleClick(itemId) {
    setIsRedirect(itemId)
  }

  function acceptInstance(instance: Instance) {
    if (isShowEdit && currentItem) {
      instance.id = currentItem.id
      fetchData(undefined, 
        instance, 
        {
          url: ExplorerPaths.INSTANCES_BY_MODEL_ID, 
          baseURL: BASE_URL
        })
      updateData(instance, (x) => x.id === instance.id);    
    } else {
      fetchData(undefined, 
        instance, 
        undefined)
    }
  }
  
  React.useEffect(() => {
    const isAvailable: boolean = !((!currentItem || currentItem == null) && isShowEdit);
    setAvailableWindow(isAvailable);
    if (!isAvailable) 
      onEditHandler()
  }, [isShowEdit])

  if (isRedirect)
    return <Redirect push to={`/explorer/${isRedirect}`}/>

  return (
    <> 
      <Container className="themed-container" fluid={true}>
        <Row>
          <BaseToolbar
            onCreate={onCreateHandler}
            onChange={onEditHandler}
            onDelete={onDeleteClick}
          />
        </Row>
        <Row>
          {data && (
            <TableInstances
              headers={headers}
              body={data}
              currentInstance={currentItem}
              onClick={onTableItemClick}
              onDoubleClick={onTableItemDoubleClick}
            />
          )}
        </Row>
      </Container>
      {isAvailableModalWindow && (
        <ModalInstance
          toggle={onCloseModalHandler}
          modal={isShowCreate || isShowEdit}
          onHandle={acceptInstance}
          text={isShowCreate ? "Создать новый экземпляр" : "Изменить экземпляр"}
          data={isShowEdit ? currentItem ? fieldValues : [] : configFields ? configFields : []}
          instanceName={isShowEdit && currentItem ? currentItem.name : undefined}
        />
      )}
    </>
  );
}

export default TableFields;
