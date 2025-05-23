import React from "react";
import useResolveType, { TypedFieldProps } from "../../hooks/resolveTypes"
import Async from 'react-async';

export default function TypedField({
  data: instanceData,
  instance,
  onUpdateDataHandler,
  onUpdateRelationHandler,
}: TypedFieldProps) {

  const resolvedFieldElement = useResolveType({
    data: instanceData,
    instance,
    onUpdateDataHandler,
    onUpdateRelationHandler
  })

  return (
    <Async promiseFn={() => resolvedFieldElement}>
      {({ data }) => {
        if (data) {
          return data;
        }
      }
    }
    </Async>);
}
