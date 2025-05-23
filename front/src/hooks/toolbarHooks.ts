import useAxios, { Options } from "axios-hooks";
import React from "react";
import { AxiosRequestConfig, AxiosError } from "axios";
import { ModelToolContextProps } from "../context/ToolbarContext";
import { Entity } from "../models/Entity";

export interface ToolbarValues<T> {
  isShowCreate: boolean;
  isShowEdit: boolean;
  loading: boolean;
  data?: T[];
  currentItem?: T;
  error?: AxiosError;
}

export interface ToolbarHandlers<T> {
  onCreateHandler(): void;
  onEditHandler(): void;
  onCheckItemHandler(
    predicate: (value: T, index: number, obj: T[]) => unknown,
    externalData?: T[]
  ): void;
  onDeleteHandler(
    filterPredicate: (value: T, index: number, array: T[]) => unknown
  ): void;
  fetchData(
    filterPredicate?: (value: T, index: number, array: T[]) => unknown,
    inData?: T,
    config?: AxiosRequestConfig
  ): void;
  onCloseModalHandler(): void;
  updateDataWithConvert(
    newData: T | T[],
    predicate?: (value: T, index: number, obj: T[]) => unknown
  ): void;
  updateData(
    newData: T | T[],
    predicate?: (value: T, index: number, obj: T[]) => unknown
  ): void;
}

export function useToolbar<T, S extends T>(
  config: AxiosRequestConfig | string,
  finderCallback: (
    current: T
  ) => (value: T, index: number, obj: T[]) => unknown,
  convertDataCallback?: (value: T | T[]) => S | S[],
  sortData?: (data: T) => void,
  options?: Options,
  init?: T[]
): [ToolbarValues<T>, ToolbarHandlers<T>] {
  const [isShowCreate, setShowCreate] = React.useState<boolean>(false);
  const [isShowEdit, setShowEdit] = React.useState<boolean>(false);
  const [data, setData] = React.useState<T[] | S[]>();
  const [currentItem, setItem] = React.useState<T>();
  const [{ data: fData, loading: loading, error: err }, refetch] = useAxios(
    config,
    options
  );

  const updateDataWithConvert = React.useCallback(
    (
      newData: T | T[],
      predicate?: (value: T, index: number, obj: T[]) => unknown
    ) => {
      if (newData) {
        if (Array.isArray(newData)) {
          const transData: S | S[] = convertDataCallback!(newData);
          Array.isArray(transData) && setData(transData);
        } else {
          if (data) {
            let replaceData = Array.from(data);
            if (predicate) {
              let obj = replaceData.find(predicate);
              if (obj) {
                const transData: S | S[] = convertDataCallback!(newData);
                Object.assign(obj, transData);
              } else {
                const transData: S | S[] = convertDataCallback!(newData);
                !Array.isArray(transData) && replaceData.push(transData);
              }
              setData(replaceData);
            }
          }
        }
      }
    },
    [data]
  );

  const updateData = React.useCallback(
    (
      newData: T | T[],
      predicate?: (value: T, index: number, obj: T[]) => unknown
    ) => {
      if (newData) {
        if (Array.isArray(newData)) {
          setData(newData);
        } else {
          if (data) {
            let replaceData = Array.from(data);
            if (predicate) {
              let obj = replaceData.find(predicate);
              if (obj) {
                Object.assign(obj, newData);
              } else {
                replaceData.push(newData);
              }
              setData(replaceData);
            }
          } else {
            setData((new Array<T>(newData)))
          }
        }
      }
    },
    [data]
  );

  React.useEffect(() => {
    sortData && sortData(fData);
    convertDataCallback
      ? updateDataWithConvert(fData, finderCallback(fData))
      : updateData(fData, finderCallback(fData));
  }, [fData]);

  const fetchData = React.useCallback(
    (
      filterPredicate?: (value: T, index: number, array: T[]) => unknown,
      inData?: T,
      config?: AxiosRequestConfig
    ) => {
      if (isShowCreate) {
        setShowCreate((v) => !v);
        inData &&
          refetch({
            ...config,
            method: "POST",
            data: transformBody(inData),
          });
      } else if (isShowEdit) {
        setShowEdit((v) => !v);
        inData &&
          refetch({
            ...config,
            method: "PUT",
            data: transformBody(inData),
          });
      } else if (currentItem && filterPredicate) {
        refetch({
          ...config,
          method: "DELETE",
          data: currentItem,
        });
        setData(data!.filter(filterPredicate));
      }
      setItem(undefined);
    },
    [isShowCreate, isShowEdit, currentItem, data]
  );

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

  const onCreateHandler = React.useCallback(() => {
    setShowCreate((v) => !v);
  }, []);

  const onEditHandler = React.useCallback(() => {
    setShowEdit((v) => !v);
  }, []);

  const onCloseModalHandler = React.useCallback(() => {
    if (isShowCreate) {
      setShowCreate((v) => !v);
    } else if (isShowEdit) {
      setShowEdit((v) => !v);
    }
  }, [isShowCreate, isShowEdit]);

  const onDeleteHandler = React.useCallback(
    (filterPredicate: (value: T, index: number, array: T[]) => unknown) => {
      fetchData(filterPredicate);
    },
    [currentItem]
  );

  const onCheckItemHandler = React.useCallback(
    (
      predicate: (value: T, index: number, obj: T[]) => unknown,
      externalData?: T[]
    ) => {
      let item: T | undefined;
      if (data) {
        item = data!.find(predicate);
      } else if (externalData) {
        item = externalData.find(predicate);
      }
      item && setItem(item);
    },
    [data]
  );

  return [
    {
      data,
      isShowCreate,
      isShowEdit,
      loading,
      error: err,
      currentItem,
    },
    {
      onCreateHandler,
      onEditHandler,
      onCheckItemHandler,
      onDeleteHandler,
      fetchData,
      onCloseModalHandler,
      updateDataWithConvert,
      updateData,
    },
  ];
}

export const useCurrentItem = (): ModelToolContextProps<Entity> => {
  const [model, setModel] = React.useState<Entity | Entity[]>();
  const [updateData, setUpdateData] = React.useState<
    (
      newData: Entity | Entity[],
      predicate?: (value: Entity, index: number, obj: Entity[]) => unknown
    ) => void
  >(() => {});

  const setCurrentModel = React.useCallback(
    (
      currentData: Entity | Entity[],
      currentUpdateData: (
        newData: Entity | Entity[],
        predicate?: (value: Entity, index: number, obj: Entity[]) => unknown
      ) => void
    ) => {
      setModel(currentData);
      setUpdateData(currentUpdateData);
    },
    []
  );

  return {
    data: model,
    updateData: updateData,
    setCurrentData: setCurrentModel,
  };
};
