import React from "react";
import Tree from "@naisutech/react-tree";
import { Entity, TreeData } from "../../models/Entity";
import { Input } from "reactstrap";
import ModalEntity from "../Modal/Modal";
import { BaseToolbar } from "../Toolbar/Toolbar";
import { AxiosRequestConfig, AxiosPromise } from "axios";
import { RefetchOptions, Options } from "axios-hooks";
import { useToolbar } from "../../hooks/toolbarHooks";
import { ManagementPaths, BASE_URL } from "../../services/managementService";
import {ModelToolContext} from "../../context/ToolbarContext"

const styles = {
  search: {
    width: "100%",
    margin: "0 0 7px 0",
  },
  entityContainer: {
    width: "100%",
    border: "3px outset #7f088c",
    borderRadius: "inherit",
    height: "100%",
    zIndex: "-1",
    overflowY: "auto",
    overflowX: "auto",
  },
};

type TreeProps = {
  data: {
    data: Entity[];
  };
  onChoosenItem(selectedNode): void;
  refetchHandler<T = any>(
    config: AxiosRequestConfig | string,
    options?: Options
  ): (config?: AxiosRequestConfig, options?: RefetchOptions) => AxiosPromise<T>;
  checkedEntity: Entity;
  onClickRemoveHandler(): void;
};

export default function TreeNaisutech<TreeProps>({
  data,
  onChoosenItem,
  refetchHandler,
  checkedEntity,
  onClickRemoveHandler,
}) {
  const setInputFocus = React.useState(false)[1];
  const [createModal, setCreateModal] = React.useState(false);
  const [editModal, setEditModal] = React.useState(false);
  const [treeData, setTreeData] = React.useState<TreeData[]>([]);

  function createToggle(): void {
    setCreateModal((m) => !m);
  }

  function editToggle(): void {
    setEditModal((m) => !m);
  }

  const editModel = (editingModel) => {
    refetchHandler({
      method: "PUT",
      data: editingModel,
    });
    editToggle();
  };

  const createModel = (newModel) => {
    refetchHandler({
      method: "POST",
      data: newModel,
    });
    createToggle();
  };

  React.useEffect(() => {
    let d: TreeData[] = [];
    data.data &&
      data.data.forEach((s: Entity) => {
        d.push({
          ...s,
          label: s.name,
          parentId: s.parent ? s.parent.id : null,
        });
      });
    setTreeData(d);
  }, [data]);

  return (
    <>
      <Input
        placeholder="Поиск модели"
        type="search"
        onFocus={(e) => setInputFocus(true)}
        onBlur={(e) => setInputFocus(false)}
        style={styles.search}
      />
      <BaseToolbar
        onCreate={createToggle}
        onChange={editToggle}
        onDelete={onClickRemoveHandler}
      />
      <div className="entity-container">
        <Tree
          onSelect={onChoosenItem}
          nodes={treeData}
          theme={"light"}
          isLoading
          noIcons
        />
      </div>
      <ModalEntity
        toggle={createToggle}
        modal={createModal}
        onHandle={createModel}
        text={"Создать новую модель"}
        parentModels={data.data}
      />
      <ModalEntity
        toggle={editToggle}
        modal={editModal}
        onHandle={editModel}
        text={"Изменить модель"}
        data={checkedEntity}
        parentModels={data.data}
      />
    </>
  );
}

export function ModelTool() {
  const setInputFocus = React.useState(false)[1];
  const [
    { data, isShowCreate, isShowEdit, loading, error, currentItem },
    {
      onCreateHandler,
      onEditHandler,
      onCheckItemHandler,
      onDeleteHandler,
      fetchData,
      onCloseModalHandler,
      updateDataWithConvert: updateData
    },
  ] = useToolbar(
    {
      url: ManagementPaths.BASE_MODEL,
      baseURL: BASE_URL,
      method: "GET",
    },
    finderPredicate,
    convertData,
    sortTreeNodes
  );
  const { setCurrentData } = React.useContext(ModelToolContext);

  React.useEffect(() => {
    currentItem && updateData && setCurrentData(currentItem, updateData)
  },[updateData, currentItem])

  function sortTreeNodes(dataArray) {
    if (dataArray && Array.isArray(dataArray)) {
      for (let i; i < dataArray.length; i++) {
        if (dataArray[i].children && dataArray[i].children != null) {
          sortTreeNodes(dataArray[i].children);
        }
      }
      dataArray.sort((a, b) => a.name.localeCompare(b.name));
    }
  }

  function convertItem(value: Entity): TreeData {
    return {
      ...value,
      label: value.name,
      parentId: value.parent ? value.parent.id : null,
    };
  }

  function convertData(value: Entity | Entity[]): TreeData | TreeData[] {
    if (Array.isArray(value)) {
      let d: TreeData[] = [];
      value.forEach((s: Entity) => {
        d.push(convertItem(s));
      });
      return d;
    } else {
      return convertItem(value);
    }
  }

  function finderPredicate(
    current: Entity
  ): (value: Entity, index: number, obj: Entity[]) => unknown {
    return (x: Entity) => x.id === current.id;
  }

  function onTreeItemClick(item: Entity) {
    onCheckItemHandler((x) => x.id === item.id);
  }

  function onDeleteClick() {
    currentItem && onDeleteHandler((x) => x.id !== currentItem.id);
  }

  return (
    <>
      {loading && <label>Loading...</label>}
      <Input
        placeholder="Поиск модели"
        type="search"
        onFocus={(e) => setInputFocus(true)}
        onBlur={(e) => setInputFocus(false)}
        style={styles.search}
      />
      <BaseToolbar
        onCreate={onCreateHandler}
        onChange={onEditHandler}
        onDelete={onDeleteClick}
      />
      {data && (
        <div className="entity-container">
          <Tree
            onSelect={onTreeItemClick}
            nodes={data}
            theme={"light"}
            isLoading
            noIcons
          />
        </div>
      )}
      <ModalEntity
        toggle={onCloseModalHandler}
        modal={isShowCreate || isShowEdit}
        onHandle={(model) => fetchData(undefined, model)}
        text={isShowCreate ? "Создать новую модель" : "Изменить модель"}
        data={isShowEdit ? currentItem : undefined}
        parentModels={data}
      />
    </>
  );
}
