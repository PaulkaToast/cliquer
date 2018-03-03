export default function genericDispatch(hasError, isLoading, success, method) {
    function specificDispatch(url, headers) {
        return (dispatch) => {
            dispatch(isLoading(true))
            fetch(url, { headers, method: method, mode: 'cors'})
                .then((response) => {
                    if (!response.ok) {
                        throw Error(response.statusText);
                    }
                    dispatch(isLoading(false));
                    return response;
                })
                .then((response) => response.json())
                .then((data) => {
                    dispatch(success(data))
                })
                .catch(() => dispatch(hasError(true)));
        }
    }
    return specificDispatch;
}